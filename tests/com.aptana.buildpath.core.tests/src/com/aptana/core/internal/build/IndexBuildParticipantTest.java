package com.aptana.core.internal.build;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.build.BuildContext;

public class IndexBuildParticipantTest
{

	private Mockery context = new Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void testTypicalBuild() throws Exception
	{
		// TODO Index multiple files, maybe mixing add/delete
		final Index index = context.mock(Index.class);
		final IFileStoreIndexingParticipant indexer = context.mock(IFileStoreIndexingParticipant.class);
		final BuildContext buildContext = context.mock(BuildContext.class);
		File file = File.createTempFile("index_bp", ".js");
		file.deleteOnExit();
		final URI uri = file.toURI();
		IndexBuildParticipant p = new IndexBuildParticipant()
		{
			@Override
			protected Index getIndex(IProject project)
			{
				return index;
			}

			@Override
			protected List<IFileStoreIndexingParticipant> getIndexParticipants(BuildContext context)
			{
				return CollectionsUtil.newList(indexer);
			}
		};
		context.checking(new Expectations()
		{
			{
				oneOf(buildContext).getURI();
				will(returnValue(uri));
				// Make sure we remove old entry for file...
				oneOf(index).remove(uri);
				// Then index files
				oneOf(indexer).index(with(buildContext), with(index), with(any(IProgressMonitor.class)));
				// Then save all changes to index
				oneOf(index).save();
			}
		});

		p.buildStarting(null, IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		p.buildFile(buildContext, new NullProgressMonitor());
		p.buildEnding(new NullProgressMonitor());
		context.assertIsSatisfied();
	}

	@Test
	public void testTypicalBuildWithDeletedFile() throws Exception
	{
		final Index index = context.mock(Index.class);
		final IFileStoreIndexingParticipant indexer = context.mock(IFileStoreIndexingParticipant.class);
		final BuildContext buildContext = context.mock(BuildContext.class);
		File file = File.createTempFile("index_bp", ".js");
		file.deleteOnExit();
		final URI uri = file.toURI();
		IndexBuildParticipant p = new IndexBuildParticipant()
		{
			@Override
			protected Index getIndex(IProject project)
			{
				return index;
			}

			@Override
			protected List<IFileStoreIndexingParticipant> getIndexParticipants(BuildContext context)
			{
				return CollectionsUtil.newList(indexer);
			}
		};
		context.checking(new Expectations()
		{
			{
				oneOf(buildContext).getURI();
				will(returnValue(uri));
				// Make sure we remove old entry for file...
				oneOf(index).remove(uri);
				// Then save all changes to index
				oneOf(index).save();
			}
		});

		p.buildStarting(null, IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
		p.deleteFile(buildContext, new NullProgressMonitor());
		p.buildEnding(new NullProgressMonitor());
		context.assertIsSatisfied();
	}

	@Test
	public void testCleanBuild() throws Exception
	{
		File file = File.createTempFile("index_bp", ".js");
		file.deleteOnExit();
		final URI uri = file.getParentFile().toURI();
		final IndexManager indexManager = context.mock(IndexManager.class);

		IndexBuildParticipant p = new IndexBuildParticipant()
		{
			@Override
			protected URI getURI(IProject project)
			{
				return uri;
			}

			@Override
			protected IndexManager getIndexManager()
			{
				return indexManager;
			}
		};
		context.checking(new Expectations()
		{
			{
				oneOf(indexManager).resetIndex(uri);
			}
		});

		p.clean(null, new NullProgressMonitor());
		context.assertIsSatisfied();
	}

}
