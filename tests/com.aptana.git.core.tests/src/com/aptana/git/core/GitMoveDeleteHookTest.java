/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.team.IResourceTree;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.ChangedFile.Status;
import com.aptana.git.core.model.GitRepository;

public class GitMoveDeleteHookTest extends TestCase
{
	private Mockery context;
	private IResourceTree tree;
	private IFile file;
	private IFolder folder;
	private IProject project;
	private GitRepository repo;
	private GitMoveDeleteHook hook;

	protected void setUp() throws Exception
	{
		super.setUp();
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		tree = context.mock(IResourceTree.class);
		file = context.mock(IFile.class);
		folder = context.mock(IFolder.class);
		project = context.mock(IProject.class);
		repo = context.mock(GitRepository.class);
		hook = new GitMoveDeleteHook()
		{
			@Override
			protected GitRepository getAttachedGitRepository(IProject project)
			{
				return repo;
			}

			protected boolean hasNoCommittedFiles(IPath source, GitRepository repo)
			{
				return false;
			}
		};
	}

	@Override
	protected void tearDown() throws Exception
	{
		try
		{
			hook = null;
			context = null;
			tree = null;
			file = null;
			project = null;
			folder = null;
			repo = null;
		}
		finally
		{
			super.tearDown();
		}
	}

	public void testDeleteFileUnforcedWithHistorySucceeds()
	{
		context.checking(new Expectations()
		{
			{
				// We're not forcing, so we need to check if file is synched
				oneOf(tree).isSynchronized(file, IResource.DEPTH_ZERO);
				will(returnValue(true));

				oneOf(file).getProject();

				oneOf(repo).getChangedFileForResource(file);
				ChangedFile changedFile = new ChangedFile("fake_path.txt", Status.MODIFIED);
				will(returnValue(changedFile));

				// keep history
				oneOf(tree).addToLocalHistory(file);

				oneOf(repo).deleteFile(changedFile.getPath());
				will(returnValue(org.eclipse.core.runtime.Status.OK_STATUS));
				// repo says we deleted ok, so we should mark that on the tree
				oneOf(tree).deletedFile(file);
			}
		});
		assertTrue(hook.deleteFile(tree, file, IResource.KEEP_HISTORY, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	public void testDeleteFileThatIsNewDoesntGoThroughRepo()
	{
		context.checking(new Expectations()
		{
			{
				// We're not forcing, so we need to check if file is synched
				oneOf(tree).isSynchronized(file, IResource.DEPTH_ZERO);
				will(returnValue(true));

				oneOf(file).getProject();

				oneOf(repo).getChangedFileForResource(file);
				ChangedFile changedFile = new ChangedFile("fake_path.txt", Status.NEW);
				will(returnValue(changedFile));
			}
		});
		assertFalse(hook.deleteFile(tree, file, IResource.KEEP_HISTORY, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	public void testDeleteFileForcedDoesntCheckSynchronized()
	{
		hook = new GitMoveDeleteHook()
		{
			@Override
			protected GitRepository getAttachedGitRepository(IProject project)
			{
				return null;
			}
		};

		context.checking(new Expectations()
		{
			{
				// We're forcing, so we don't need to check if file is synched
				never(tree).isSynchronized(file, IResource.DEPTH_ZERO);
				oneOf(file).getProject();
			}
		});
		assertFalse(hook.deleteFile(tree, file, IResource.FORCE, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	public void testDeleteFileFailsInGitRepo()
	{
		context.checking(new Expectations()
		{
			{
				// We're not forcing, so we need to check if file is synched
				oneOf(tree).isSynchronized(file, IResource.DEPTH_ZERO);
				will(returnValue(true));

				oneOf(file).getProject();

				oneOf(repo).getChangedFileForResource(file);
				ChangedFile changedFile = new ChangedFile("fake_path.txt", Status.MODIFIED);
				will(returnValue(changedFile));

				oneOf(repo).deleteFile(changedFile.getPath());
				IStatus status = org.eclipse.core.runtime.Status.CANCEL_STATUS;
				will(returnValue(status));
				// repo says we deleted ok, so we should mark that on the tree
				oneOf(tree).failed(status);
			}
		});
		// should still return true, because we tried, but failed
		assertTrue(hook.deleteFile(tree, file, IResource.NONE, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	public void testDeleteFolderUnforcedWithHistorySucceeds()
	{
		final boolean[] addFilesToHistoryCalled = new boolean[1];
		addFilesToHistoryCalled[0] = false;
		hook = new GitMoveDeleteHook()
		{
			@Override
			protected GitRepository getAttachedGitRepository(IProject project)
			{
				return repo;
			}

			protected void addFilesToLocalHistoryRecursively(IResourceTree tree, IContainer folder)
			{
				// Make sure we get called here!
				addFilesToHistoryCalled[0] = true;
			}

			protected boolean hasNoCommittedFiles(IPath source, GitRepository repo)
			{
				return false;
			}

		};
		context.checking(new Expectations()
		{
			{
				// We're not forcing, so we need to check if file is synched
				oneOf(tree).isSynchronized(folder, IResource.DEPTH_INFINITE);
				will(returnValue(true));

				oneOf(folder).getProject();

				// check for .git
				oneOf(folder).getName();
				will(returnValue("folder"));

				// repo relative path
				oneOf(repo).workingDirectory();

				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root")));
				oneOf(folder).getLocation();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root" + File.separator + "folder")));

				// check for committed files

				// local history

				// delete via repo
				oneOf(repo).deleteFolder(Path.fromOSString("folder"));
				will(returnValue(org.eclipse.core.runtime.Status.OK_STATUS));
				// repo says we deleted ok, so we should mark that on the tree
				oneOf(tree).deletedFolder(folder);
			}
		});
		assertTrue(hook.deleteFolder(tree, folder, IResource.KEEP_HISTORY, new NullProgressMonitor()));
		assertTrue("Should have tried to save file history", addFilesToHistoryCalled[0]);
		context.assertIsSatisfied();
	}

	public void testDeleteFolderForcedChecksSynch()
	{
		hook = new GitMoveDeleteHook()
		{
			@Override
			protected GitRepository getAttachedGitRepository(IProject project)
			{
				return null;
			}
		};
		context.checking(new Expectations()
		{
			{
				// We're forcing, so we don't need to check if file is synched
				never(tree).isSynchronized(folder, IResource.DEPTH_INFINITE);
				oneOf(folder).getProject();
			}
		});
		assertFalse(hook.deleteFolder(tree, folder, IResource.FORCE, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	public void testPuntsDeleteFolderWhenUnderGitRepoButUncommitted()
	{
		hook = new GitMoveDeleteHook()
		{
			@Override
			protected GitRepository getAttachedGitRepository(IProject project)
			{
				return repo;
			}

			protected boolean hasNoCommittedFiles(IPath source, GitRepository repo)
			{
				return true;
			}
		};
		context.checking(new Expectations()
		{
			{
				// We're not forcing, so we need to check if file is synched
				oneOf(tree).isSynchronized(folder, IResource.DEPTH_INFINITE);
				will(returnValue(true));

				// get Repo
				oneOf(folder).getProject();

				// check name for .git
				oneOf(folder).getName();
				will(returnValue("folder"));

				// repo relative path
				oneOf(repo).workingDirectory();

				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root")));
				oneOf(folder).getLocation();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root" + File.separator + "folder")));

				// We don't try these because we punted
				never(repo).deleteFolder(Path.fromOSString("folder"));
				never(tree).deletedFolder(folder);
			}
		});
		assertFalse(hook.deleteFolder(tree, folder, IResource.NONE, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	public void testWontDeleteDotGitFolderIfRepoIsAttached()
	{
		context.checking(new Expectations()
		{
			{
				// We're not forcing, so we need to check if file is synched
				oneOf(tree).isSynchronized(folder, IResource.DEPTH_INFINITE);
				will(returnValue(true));

				oneOf(folder).getProject();

				oneOf(folder).getName();
				will(returnValue(GitRepository.GIT_DIR));

				oneOf(tree).failed(GitMoveDeleteHook.CANNOT_MODIFY_REPO);
			}
		});
		// Should return true to indicate we handled the delete request (though in this case we punted because it's
		// unsafe)
		assertTrue(hook.deleteFolder(tree, folder, IResource.NONE, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	public void testWillLetFilesystemAPIDeleteDotGitFolderIfRepoIsNotAttached()
	{
		hook = new GitMoveDeleteHook()
		{
			@Override
			protected GitRepository getAttachedGitRepository(IProject project)
			{
				return null;
			}
		};
		context.checking(new Expectations()
		{
			{
				oneOf(folder).getProject();
			}
		});
		// Should return false to tell the normal filesystem to delete it
		assertFalse(hook.deleteFolder(tree, folder, IResource.FORCE, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	// Add test to ensure that we don't delete .git dir if user doesn't check to delete project!
	public void testDeleteProjectButNotContentsDoesntDeleteGitDir()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(repo).workingDirectory();

				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root")));

				oneOf(project).getLocation();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root" + File.separator
						+ "project")));

				// Repo relative path
				oneOf(repo).workingDirectory();

				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root")));
				oneOf(project).getLocation();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root" + File.separator
						+ "project")));
								
				// Don't ever delete .git folder!
				never(tree).standardDeleteFolder(folder, IResource.DEPTH_INFINITE, null);

				// We're forcing, so no need to check if file is synched
				never(tree).isSynchronized(project, IResource.DEPTH_INFINITE);		

				oneOf(project).isOpen();
				will(returnValue(true));

				// Now actually delete contents
				never(repo).deleteFolder(Path.fromOSString("project"));
				
				// repo says we deleted ok, so we should mark that on the tree
				oneOf(tree).deletedProject(project);
			}
		});
		assertTrue(hook.deleteProject(tree, project, IResource.NEVER_DELETE_PROJECT_CONTENT | IResource.FORCE, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}
	
	public void testDeleteProjectUnforcedSucceeds()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(repo).workingDirectory();

				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root")));

				oneOf(project).getLocation();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root" + File.separator
						+ "project")));

				// Repo relative path
				oneOf(repo).workingDirectory();

				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root")));
				oneOf(project).getLocation();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root" + File.separator
						+ "project")));

				// We're not forcing, so we need to check if file is synched
				oneOf(tree).isSynchronized(project, IResource.DEPTH_INFINITE);
				will(returnValue(true));

				oneOf(project).isOpen();
				will(returnValue(true));

				// Now actually delete contents
				oneOf(repo).deleteFolder(Path.fromOSString("project"));
				will(returnValue(org.eclipse.core.runtime.Status.OK_STATUS));
				// repo says we deleted ok, so we should mark that on the tree
				oneOf(tree).deletedProject(project);
			}
		});
		assertTrue(hook.deleteProject(tree, project, 0, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	public void testDeleteProjectUnforcedUnsynchedReturnsFalse()
	{
		context.checking(new Expectations()
		{
			{
				exactly(2).of(repo).workingDirectory();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root")));

				exactly(2).of(project).getLocation();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root" + File.separator
						+ "project")));

				// We're not forcing, so we need to check if file is synched
				oneOf(tree).isSynchronized(project, IResource.DEPTH_INFINITE);
				will(returnValue(false));

				// should never actually try to delete the project
				never(repo).deleteFolder(Path.fromOSString("project"));
			}
		});
		assertFalse(hook.deleteProject(tree, project, 0, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	public void testDeleteProjectWhenUnderGitRepoButUncommitted()
	{
		hook = new GitMoveDeleteHook()
		{
			@Override
			protected GitRepository getAttachedGitRepository(IProject project)
			{
				return repo;
			}

			protected boolean hasNoCommittedFiles(IPath source, GitRepository repo)
			{
				return true;
			}
		};
		context.checking(new Expectations()
		{
			{
				exactly(2).of(repo).workingDirectory();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root")));

				exactly(2).of(project).getLocation();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root" + File.separator
						+ "project")));

				never(repo).deleteFolder(Path.fromOSString("project"));
			}
		});
		assertFalse(hook.deleteProject(tree, project, IResource.ALWAYS_DELETE_PROJECT_CONTENT,
				new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	public void testPuntsOnDeleteProjectWhenItIsNotAttachedToGit()
	{
		hook = new GitMoveDeleteHook()
		{
			@Override
			protected GitRepository getAttachedGitRepository(IProject project)
			{
				return null;
			}
		};
		assertFalse(hook.deleteProject(tree, project, IResource.ALWAYS_DELETE_PROJECT_CONTENT,
				new NullProgressMonitor()));
	}

	public void testPuntsOnDeleteProjectWhenItIsGitRoot()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(repo).workingDirectory();

				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root")));

				oneOf(project).getLocation();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root")));

				oneOf(project).getLocationURI();

				oneOf(project).getFolder(GitRepository.GIT_DIR);
				will(returnValue(folder));

				oneOf(folder).exists();
				will(returnValue(true));

				oneOf(tree).standardDeleteFolder(with(equal(folder)),
						with(equal(IResource.ALWAYS_DELETE_PROJECT_CONTENT | IResource.FORCE)),
						with(any(NullProgressMonitor.class)));

				oneOf(tree).deletedFolder(folder);
			}
		});
		assertFalse(hook.deleteProject(tree, project, IResource.ALWAYS_DELETE_PROJECT_CONTENT,
				new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	// TODO Add tests for moving files
	// TODO Add tests for moving folders
	// TODO Add tests for moving projects
}
