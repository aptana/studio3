package com.aptana.core.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.internal.build.BuildParticipantManager;
import com.aptana.core.resources.IMarkerConstants;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.index.core.build.BuildContext;
import com.aptana.testing.utils.ProjectCreator;

public class UnifiedBuilderTest
{

	private static final class FileDelta implements IResourceDelta
	{
		private final IFile file;
		private int kind;

		private FileDelta(IFile file, int kind)
		{
			this.file = file;
			this.kind = kind;
		}

		public Object getAdapter(Class adapter)
		{
			return null;
		}

		public void accept(IResourceDeltaVisitor visitor) throws CoreException
		{
			visitor.visit(this);
		}

		public void accept(IResourceDeltaVisitor visitor, boolean includePhantoms) throws CoreException
		{
		}

		public void accept(IResourceDeltaVisitor visitor, int memberFlags) throws CoreException
		{
		}

		public IResourceDelta findMember(IPath path)
		{
			return null;
		}

		public IResourceDelta[] getAffectedChildren()
		{
			return null;
		}

		public IResourceDelta[] getAffectedChildren(int kindMask)
		{
			return null;
		}

		public IResourceDelta[] getAffectedChildren(int kindMask, int memberFlags)
		{
			return null;
		}

		public int getFlags()
		{
			return 0;
		}

		public IPath getFullPath()
		{
			return file.getFullPath();
		}

		public int getKind()
		{
			return kind;
		}

		public IMarkerDelta[] getMarkerDeltas()
		{
			return null;
		}

		public IPath getMovedFromPath()
		{
			return null;
		}

		public IPath getMovedToPath()
		{
			return null;
		}

		public IPath getProjectRelativePath()
		{
			return file.getProjectRelativePath();
		}

		public IResource getResource()
		{
			return file;
		}
	}

	private UnifiedBuilder builder;
	private IBuildParticipantManager manager;
	private IBuildParticipant participant;
	private IProject project;
	private IResourceDelta delta;

	private Mockery context = new Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		project = ProjectCreator.createAndOpen("project_to_build");
		participant = context.mock(IBuildParticipant.class);
		manager = new BuildParticipantManager()
		{
			public List<IBuildParticipant> getBuildParticipants(String contentTypeId)
			{
				return filterParticipants(getAllBuildParticipants(), contentTypeId);
			}

			public List<IBuildParticipant> getAllBuildParticipants()
			{
				return CollectionsUtil.newList(participant);
			}
		};
		builder = new UnifiedBuilder()
		{
			@Override
			protected IProject getProjectHandle()
			{
				return project;
			}

			@Override
			protected IResourceDelta getResourceDelta()
			{
				return delta;
			}

			@Override
			protected IBuildParticipantManager getBuildParticipantManager()
			{
				return manager;
			}

			@Override
			protected boolean traceLoggingEnabled()
			{
				return true;
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		if (project != null)
		{
			project.delete(IResource.FORCE, new NullProgressMonitor());
			project = null;
		}
		participant = null;
		builder = null;
		manager = null;
//		super.tearDown();
	}

	@Test
	public void testFullBuild() throws Exception
	{
		IFolder folder = project.getFolder("folder");
		folder.create(true, true, null);

		final String fileName = "subfile.txt";
		IFile file = folder.getFile(fileName);
		file.create(new ByteArrayInputStream("Hello world!".getBytes()), true, null);

		context.checking(new Expectations()
		{
			{
				allowing(participant).isEnabled(BuildType.BUILD);
				will(returnValue(true));
				allowing(participant).isEnabled(project);
				will(returnValue(true));
				allowing(participant).getContentTypes();
				will(returnValue(Collections.emptySet()));

				// Call build starting
				oneOf(participant).buildStarting(with(project), with(IncrementalProjectBuilder.FULL_BUILD),
						with(any(IProgressMonitor.class)));
				// build the .project file
				oneOf(participant).buildFile(
						(BuildContext) with(Matchers.allOf(Matchers.is(BuildContext.class),
								Matchers.hasProperty("name", equal(IProjectDescription.DESCRIPTION_FILE_NAME)))),
						with(any(IProgressMonitor.class)));
				// build the subfile
				oneOf(participant).buildFile(
						(BuildContext) with(Matchers.allOf(Matchers.is(BuildContext.class),
								Matchers.hasProperty("name", equal(fileName)))), with(any(IProgressMonitor.class)));
				// build ending
				oneOf(participant).buildEnding(with(any(IProgressMonitor.class)));
			}
		});
		builder.build(IncrementalProjectBuilder.FULL_BUILD, null, new NullProgressMonitor());
		context.assertIsSatisfied();
	}

	@Test
	public void testFullBuild2() throws Exception
	{
		final String taskMessage = "Fake task";
		final Integer line = 1;
		final Integer offset = 0;
		final Integer endOffset = 9;
		participant = new RequiredBuildParticipant()
		{

			public void deleteFile(BuildContext context, IProgressMonitor monitor)
			{
				// TODO Auto-generated method stub

			}

			public void buildFile(BuildContext context, IProgressMonitor monitor)
			{
				Collection<IProblem> problems = new ArrayList<IProblem>();
				problems.add(createTask(context.getURI().toString(), taskMessage, IMarker.PRIORITY_HIGH, line, offset,
						endOffset));
				context.putProblems(IMarkerConstants.TASK_MARKER, problems);
			}
		};
		builder.build(IncrementalProjectBuilder.FULL_BUILD, null, new NullProgressMonitor());

		// Assert that the task marker got written to the file
		IFile file = project.getFile(IProjectDescription.DESCRIPTION_FILE_NAME);
		IMarker[] markers = file.findMarkers(IMarkerConstants.TASK_MARKER, true, IResource.DEPTH_ZERO);
		assertNotNull(markers);
		assertEquals(1, markers.length);
		Map<String, Object> attributes = markers[0].getAttributes();
		assertEquals(taskMessage, attributes.get(IMarker.MESSAGE));
		assertEquals(line, attributes.get(IMarker.LINE_NUMBER));
		assertEquals(offset, attributes.get(IMarker.CHAR_START));
		assertEquals(endOffset, attributes.get(IMarker.CHAR_END));

		// Now clean...
		builder.clean(new NullProgressMonitor());
		// Make sure it cleaned out the task
		markers = file.findMarkers(IMarkerConstants.TASK_MARKER, true, IResource.DEPTH_ZERO);
		assertNotNull(markers);
		assertEquals(0, markers.length);

		// TODO Add tests that verify when we clean the project we clean out our own marker sub-types, but not the base
		// PROBLEM/TASK types?
	}

	@Test
	public void testIncrementalBuildWithNoDeltaDoesFullBuild() throws Exception
	{
		context.checking(new Expectations()
		{
			{
				allowing(participant).isEnabled(BuildType.BUILD);
				will(returnValue(true));
				allowing(participant).isEnabled(project);
				will(returnValue(true));
				allowing(participant).getContentTypes();
				will(returnValue(Collections.emptySet()));

				// Call build starting
				oneOf(participant).buildStarting(with(project), with(IncrementalProjectBuilder.INCREMENTAL_BUILD),
						with(any(IProgressMonitor.class)));
				// build the .project file

				oneOf(participant).buildFile(
						(BuildContext) with(Matchers.allOf(Matchers.is(BuildContext.class),
								Matchers.hasProperty("name", equal(IProjectDescription.DESCRIPTION_FILE_NAME)))),
						with(any(IProgressMonitor.class)));
				// build ending
				oneOf(participant).buildEnding(with(any(IProgressMonitor.class)));
			}
		});
		builder.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null, new NullProgressMonitor());
		context.assertIsSatisfied();
	}

	@Test
	public void testIncrementalBuildWithDeletedFileDelta() throws Exception
	{
		IFile file = project.getFile(IProjectDescription.DESCRIPTION_FILE_NAME);
		delta = new FileDelta(file, IResourceDelta.REMOVED);

		context.checking(new Expectations()
		{
			{
				allowing(participant).isEnabled(BuildType.BUILD);
				will(returnValue(true));
				allowing(participant).isEnabled(project);
				will(returnValue(true));
				allowing(participant).getContentTypes();
				will(returnValue(Collections.emptySet()));

				// Call build starting
				oneOf(participant).buildStarting(with(project), with(IncrementalProjectBuilder.INCREMENTAL_BUILD),
						with(any(IProgressMonitor.class)));
				// delete the .project file
				oneOf(participant).deleteFile(
						(BuildContext) with(Matchers.allOf(Matchers.is(BuildContext.class),
								Matchers.hasProperty("name", equal(IProjectDescription.DESCRIPTION_FILE_NAME)))),
						with(any(IProgressMonitor.class)));
				// build ending
				oneOf(participant).buildEnding(with(any(IProgressMonitor.class)));
			}
		});
		builder.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null, new NullProgressMonitor());
		context.assertIsSatisfied();
	}

	@Test
	public void testIncrementalBuildWithAddededFileDelta() throws Exception
	{
		IFile file = project.getFile(IProjectDescription.DESCRIPTION_FILE_NAME);
		delta = new FileDelta(file, IResourceDelta.ADDED);

		context.checking(new Expectations()
		{
			{
				allowing(participant).isEnabled(BuildType.BUILD);
				will(returnValue(true));
				allowing(participant).isEnabled(project);
				will(returnValue(true));
				allowing(participant).getContentTypes();
				will(returnValue(Collections.emptySet()));

				// Call build starting
				oneOf(participant).buildStarting(with(project), with(IncrementalProjectBuilder.INCREMENTAL_BUILD),
						with(any(IProgressMonitor.class)));
				// build the .project file
				oneOf(participant).buildFile(
						(BuildContext) with(Matchers.allOf(Matchers.is(BuildContext.class),
								Matchers.hasProperty("name", equal(IProjectDescription.DESCRIPTION_FILE_NAME)))),
						with(any(IProgressMonitor.class)));
				// build ending
				oneOf(participant).buildEnding(with(any(IProgressMonitor.class)));
			}
		});
		builder.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null, new NullProgressMonitor());
		context.assertIsSatisfied();
	}

	@Test
	public void testClean() throws Exception
	{
		context.checking(new Expectations()
		{
			{
				allowing(participant).isEnabled(BuildType.BUILD);
				will(returnValue(true));
				allowing(participant).isEnabled(project);
				will(returnValue(true));
				allowing(participant).getContentTypes();
				will(returnValue(Collections.emptySet()));

				// Call clean on project
				oneOf(participant).clean(with(project), with(any(IProgressMonitor.class)));
			}
		});
		builder.clean(new NullProgressMonitor());
		context.assertIsSatisfied();
	}

	@Test
	public void testDontBuildDerivedFiles() throws Exception
	{
		IFile file = project.getFile(IProjectDescription.DESCRIPTION_FILE_NAME);
		file.setDerived(true, null);

		context.checking(new Expectations()
		{
			{
				allowing(participant).isEnabled(BuildType.BUILD);
				will(returnValue(true));
				allowing(participant).isEnabled(project);
				will(returnValue(true));
				allowing(participant).getContentTypes();
				will(returnValue(Collections.emptySet()));

				// Call build starting
				oneOf(participant).buildStarting(with(project), with(IncrementalProjectBuilder.FULL_BUILD),
						with(any(IProgressMonitor.class)));
				// Don't build the .project file
				never(participant).buildFile(
						(BuildContext) with(Matchers.allOf(Matchers.is(BuildContext.class),
								Matchers.hasProperty("name", equal(IProjectDescription.DESCRIPTION_FILE_NAME)))),
						with(any(IProgressMonitor.class)));
				// build ending
				oneOf(participant).buildEnding(with(any(IProgressMonitor.class)));
			}
		});
		builder.build(IncrementalProjectBuilder.FULL_BUILD, null, new NullProgressMonitor());
		context.assertIsSatisfied();
	}

	@Test
	public void testDontBuildFilesUnderDerivedAncestor() throws Exception
	{
		IFolder folder = project.getFolder("build");
		folder.create(true, true, null);

		final String subFileName = "subfile.txt";
		IFile subFile = folder.getFile(subFileName);
		subFile.create(new ByteArrayInputStream("Hello world!".getBytes()), true, null);
		folder.setDerived(true, null);

		context.checking(new Expectations()
		{
			{
				allowing(participant).isEnabled(BuildType.BUILD);
				will(returnValue(true));
				allowing(participant).isEnabled(project);
				will(returnValue(true));
				allowing(participant).getContentTypes();
				will(returnValue(Collections.emptySet()));

				// Call build starting
				oneOf(participant).buildStarting(with(project), with(IncrementalProjectBuilder.FULL_BUILD),
						with(any(IProgressMonitor.class)));
				// Build the .project file
				oneOf(participant).buildFile(
						(BuildContext) with(Matchers.allOf(Matchers.is(BuildContext.class),
								Matchers.hasProperty("name", equal(IProjectDescription.DESCRIPTION_FILE_NAME)))),
						with(any(IProgressMonitor.class)));
				// Don't build the sub-file
				never(participant).buildFile(
						(BuildContext) with(Matchers.allOf(Matchers.is(BuildContext.class),
								Matchers.hasProperty("name", equal(subFileName)))), with(any(IProgressMonitor.class)));
				// build ending
				oneOf(participant).buildEnding(with(any(IProgressMonitor.class)));
			}
		});
		builder.build(IncrementalProjectBuilder.FULL_BUILD, null, new NullProgressMonitor());
		context.assertIsSatisfied();
	}

	@Test
	public void testDontBuildTeamPrivateFiles() throws Exception
	{
		IFile file = project.getFile(IProjectDescription.DESCRIPTION_FILE_NAME);
		file.setTeamPrivateMember(true);

		context.checking(new Expectations()
		{
			{
				allowing(participant).isEnabled(BuildType.BUILD);
				will(returnValue(true));
				allowing(participant).isEnabled(project);
				will(returnValue(true));
				allowing(participant).getContentTypes();
				will(returnValue(Collections.emptySet()));

				// Call build starting
				oneOf(participant).buildStarting(with(project), with(IncrementalProjectBuilder.FULL_BUILD),
						with(any(IProgressMonitor.class)));
				// Don't build the .project file
				never(participant).buildFile(
						(BuildContext) with(Matchers.allOf(Matchers.is(BuildContext.class),
								Matchers.hasProperty("name", equal(IProjectDescription.DESCRIPTION_FILE_NAME)))),
						with(any(IProgressMonitor.class)));
				// build ending
				oneOf(participant).buildEnding(with(any(IProgressMonitor.class)));
			}
		});
		builder.build(IncrementalProjectBuilder.FULL_BUILD, null, new NullProgressMonitor());
		context.assertIsSatisfied();
	}

	@Test
	public void testDontBuildFilesUnderTeamPrivateAncestor() throws Exception
	{
		IFolder folder = project.getFolder("git");
		folder.create(true, true, null);
		folder.setTeamPrivateMember(true);

		final String subFileName = "subfile.txt";
		IFile subFile = folder.getFile(subFileName);
		subFile.create(new ByteArrayInputStream("Hello world!".getBytes()), true, null);

		context.checking(new Expectations()
		{
			{
				allowing(participant).isEnabled(BuildType.BUILD);
				will(returnValue(true));
				allowing(participant).isEnabled(project);
				will(returnValue(true));
				allowing(participant).getContentTypes();
				will(returnValue(Collections.emptySet()));

				// Call build starting
				oneOf(participant).buildStarting(with(project), with(IncrementalProjectBuilder.FULL_BUILD),
						with(any(IProgressMonitor.class)));
				// Build the .project file
				oneOf(participant).buildFile(
						(BuildContext) with(Matchers.allOf(Matchers.is(BuildContext.class),
								Matchers.hasProperty("name", equal(IProjectDescription.DESCRIPTION_FILE_NAME)))),
						with(any(IProgressMonitor.class)));
				// Don't build the sub-file
				never(participant).buildFile(
						(BuildContext) with(Matchers.allOf(Matchers.is(BuildContext.class),
								Matchers.hasProperty("name", equal(subFileName)))), with(any(IProgressMonitor.class)));
				// build ending
				oneOf(participant).buildEnding(with(any(IProgressMonitor.class)));
			}
		});
		builder.build(IncrementalProjectBuilder.FULL_BUILD, null, new NullProgressMonitor());
		context.assertIsSatisfied();
	}
}
