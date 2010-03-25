package com.aptana.git.core;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.team.IResourceTree;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
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
	private GitRepository repo;

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
		repo = context.mock(GitRepository.class);
	}

	@Override
	protected void tearDown() throws Exception
	{
		try
		{
			context = null;
			tree = null;
			file = null;
			repo = null;
		}
		finally
		{
			super.tearDown();
		}
	}

	public void testDeleteFileUnforcedWithHistorySucceeds()
	{
		GitMoveDeleteHook hook = new GitMoveDeleteHook()
		{
			@Override
			protected GitRepository getAttachedGitRepository(IProject project)
			{
				return repo;
			}
		};

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

	public void testDeleteFileForcedDoesntCheckSynchronized()
	{
		GitMoveDeleteHook hook = new GitMoveDeleteHook()
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
		GitMoveDeleteHook hook = new GitMoveDeleteHook()
		{
			@Override
			protected GitRepository getAttachedGitRepository(IProject project)
			{
				return repo;
			}
		};

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
		// shoudl still return true, because we tried, but failed
		assertTrue(hook.deleteFile(tree, file, IResource.NONE, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}
}
