/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.team.IResourceTree;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRepositoryManager;
import com.aptana.git.core.model.IGitRepositoryManager;

public class GitMoveDeleteHookTest
{
	private Mockery context;
	private IResourceTree tree;
	private IFile file;
	private IFolder folder;
	private IProject project;
	private GitRepository repo;
	private GitRepositoryManager repoManager;
	private GitMoveDeleteHook hook;

	@Before
	public void setUp() throws Exception
	{
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
		repoManager = context.mock(GitRepositoryManager.class);
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

	@After
	public void tearDown() throws Exception
	{
		hook = null;
		context = null;
		tree = null;
		file = null;
		project = null;
		folder = null;
		repo = null;
	}

	@Test
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
				ChangedFile changedFile = new ChangedFile(null, Path.fromPortableString("fake_path.txt"),
						ChangedFile.Status.MODIFIED, null, null, false, false);
				will(returnValue(changedFile));

				// keep history
				oneOf(tree).addToLocalHistory(file);

				oneOf(repo).deleteFile(changedFile.getRelativePath());
				will(returnValue(org.eclipse.core.runtime.Status.OK_STATUS));
				// repo says we deleted ok, so we should mark that on the tree
				oneOf(tree).deletedFile(file);
			}
		});
		assertTrue(hook.deleteFile(tree, file, IResource.KEEP_HISTORY, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	@Test
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
				ChangedFile changedFile = new ChangedFile(null, Path.fromPortableString("fake_path.txt"),
						ChangedFile.Status.NEW, null, null, false, false);
				will(returnValue(changedFile));
			}
		});
		assertFalse(hook.deleteFile(tree, file, IResource.KEEP_HISTORY, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	@Test
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

	@Test
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
				ChangedFile changedFile = new ChangedFile(null, Path.fromPortableString("fake_path.txt"),
						ChangedFile.Status.MODIFIED, null, null, false, false);
				will(returnValue(changedFile));

				oneOf(repo).deleteFile(changedFile.getRelativePath());
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

	@Test
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
				oneOf(repo).relativePath(folder);
				will(returnValue(Path.fromOSString("folder")));

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

	@Test
	public void testDeleteFolderForcedNeverChecksSynch()
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

	@Test
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
				oneOf(repo).relativePath(folder);
				will(returnValue(Path.fromOSString("folder")));

				// We don't try these because we punted
				never(repo).deleteFolder(Path.fromOSString("folder"));
				never(tree).deletedFolder(folder);
			}
		});
		assertFalse(hook.deleteFolder(tree, folder, IResource.NONE, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	@Test
	public void testCallsStandardDeleteFolderWhenFilesUnderneathAreCommittedButAlreadyDeletedFromDisk()
	{
		final IProgressMonitor monitor = new NullProgressMonitor();
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

				// get repo root
				oneOf(repo).relativePath(folder);
				will(returnValue(Path.fromOSString("folder")));

				// We try to delete the folder through git...
				oneOf(repo).deleteFolder(Path.fromOSString("folder"));
				// git returns an error that nothing matched the path
				will(returnValue(new Status(IStatus.ERROR, GitPlugin.getPluginId(), 128,
						"fatal: pathspec 'folder' did not match any files", null)));

				// We fall back to standard deletion
				oneOf(tree).standardDeleteFolder(folder, IResource.NONE, monitor);

				// we never mark the folder deleted ourselves, because we punted...
				never(tree).deletedFolder(folder);
			}
		});
		assertTrue(hook.deleteFolder(tree, folder, IResource.NONE, monitor));
		context.assertIsSatisfied();
	}

	@Test
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

	@Test
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
	@Test
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
				oneOf(repo).relativePath(project);
				will(returnValue(Path.fromOSString("project")));

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
		assertTrue(hook.deleteProject(tree, project, IResource.NEVER_DELETE_PROJECT_CONTENT | IResource.FORCE,
				new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	@Test
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
				oneOf(repo).relativePath(project);
				will(returnValue(Path.fromOSString("project")));

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

	@Test
	public void testDeleteProjectUnforcedUnsynchedReturnsFalse()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(repo).workingDirectory();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root")));

				oneOf(repo).relativePath(project);
				will(returnValue(Path.fromOSString("project")));

				oneOf(project).getLocation();
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

	@Test
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
				oneOf(repo).workingDirectory();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root")));

				oneOf(repo).relativePath(project);
				will(returnValue(Path.fromOSString("project")));

				oneOf(project).getLocation();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root" + File.separator
						+ "project")));

				never(repo).deleteFolder(Path.fromOSString("project"));
			}
		});
		assertFalse(hook.deleteProject(tree, project, IResource.ALWAYS_DELETE_PROJECT_CONTENT,
				new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	@Test
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

	@Test
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
	@Test
	public void testMoveProjectWhenProjectRootIsRootOfRepo() throws Exception
	{
		hook = new GitMoveDeleteHook()
		{
			@Override
			protected GitRepository getAttachedGitRepository(IProject project)
			{
				return repo;
			}

			@Override
			protected IGitRepositoryManager getGitRepositoryManager()
			{
				return repoManager;
			}
		};
		final IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription("newName");
		context.checking(new Expectations()
		{
			{
				oneOf(repo).workingDirectory();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root" + File.separator
						+ "project")));

				oneOf(project).getLocation();
				will(returnValue(Path.fromOSString(File.separator + "some" + File.separator + "root" + File.separator
						+ "project")));

				// Verify we remove our git support
				oneOf(repoManager).removeRepository(project);

				// Let eclipse do the actual moving of files/project
				oneOf(tree).standardMoveProject(with(same(project)), with(same(description)),
						with(equal(IResource.FORCE)), with(any(NullProgressMonitor.class)));

				// then we re-attach our support
				oneOf(repoManager).attachExisting(with(same(project)), with(any(NullProgressMonitor.class)));

				// Don't ever try to move files ourselves
				never(repo).moveFile(with(any(Path.class)), with(any(Path.class)));
			}
		});

		assertTrue(hook.moveProject(tree, project, description, IResource.FORCE, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	@Test
	public void testMoveProjectWithNoGitRepoPunts() throws Exception
	{
		hook = new GitMoveDeleteHook()
		{
			@Override
			protected GitRepository getAttachedGitRepository(IProject project)
			{
				return null;
			}
		};
		final IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription("newName");
		assertFalse(hook.moveProject(tree, project, description, IResource.FORCE, new NullProgressMonitor()));
	}

	@Test
	public void testMoveProjectWhenProjectIsChildOfRepoRoot() throws Exception
	{
		hook = new GitMoveDeleteHook()
		{
			@Override
			protected GitRepository getAttachedGitRepository(IProject project)
			{
				return repo;
			}

			@Override
			protected boolean hasNoCommittedFiles(IPath source, GitRepository repo)
			{
				return false;
			}

			@Override
			protected IGitRepositoryManager getGitRepositoryManager()
			{
				return repoManager;
			}
		};
		final IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription("newName");
		description.setLocation(Path.fromPortableString("/some/root/newName"));
		context.checking(new Expectations()
		{
			{
				oneOf(repo).workingDirectory();
				will(returnValue(Path.fromPortableString("/some/root")));

				oneOf(project).getLocation();
				will(returnValue(Path.fromPortableString("/some/root/project")));

				// Verify we NEVER remove our git support
				never(repoManager).removeRepository(project);

				// We DO NOT let eclipse do the actual moving of files/project
				never(tree).standardMoveProject(with(same(project)), with(same(description)),
						with(equal(IResource.FORCE)), with(any(NullProgressMonitor.class)));

				// We grab the relative path of the project to root of repo
				oneOf(repo).relativePath(project);
				will(returnValue(Path.fromPortableString("project")));

				// We treat the moving of the project as a moved subfolder of git
				oneOf(repo).moveFile(with(equal(Path.fromPortableString("project"))),
						with(equal(Path.fromPortableString("newName"))));
				will(returnValue(Status.OK_STATUS));

				// We let eclipse know we moved the project
				oneOf(tree).movedProjectSubtree(project, description);
			}
		});

		assertTrue(hook.moveProject(tree, project, description, IResource.FORCE, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}

	@Test
	public void testMoveProjectWhenProjectIsChildOfRepoRootAndDestinationIsOutsideRepo() throws Exception
	{
		hook = new GitMoveDeleteHook()
		{
			@Override
			protected GitRepository getAttachedGitRepository(IProject project)
			{
				return repo;
			}

			@Override
			protected boolean hasNoCommittedFiles(IPath source, GitRepository repo)
			{
				return false;
			}

			@Override
			protected IGitRepositoryManager getGitRepositoryManager()
			{
				return repoManager;
			}
		};
		final IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription("newName");
		description.setLocation(Path.fromPortableString("/usr/elsewhere/newName"));
		context.checking(new Expectations()
		{
			{
				oneOf(repo).workingDirectory();
				will(returnValue(Path.fromPortableString("/some/root")));

				oneOf(project).getLocation();
				will(returnValue(Path.fromPortableString("/some/root/project")));

				// Verify we remove our git support
				oneOf(repoManager).removeRepository(project);

				// We let eclipse do the actual moving of files/project
				oneOf(tree).standardMoveProject(with(same(project)), with(same(description)),
						with(equal(IResource.FORCE)), with(any(NullProgressMonitor.class)));

				// We never re-attach
				never(repoManager).attachExisting(with(same(project)), with(any(NullProgressMonitor.class)));
			}
		});

		assertTrue(hook.moveProject(tree, project, description, IResource.FORCE, new NullProgressMonitor()));
		context.assertIsSatisfied();
	}
}
