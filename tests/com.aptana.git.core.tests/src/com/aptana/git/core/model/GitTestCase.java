package com.aptana.git.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;

import com.aptana.core.util.FileUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.ChangedFile.Status;

public abstract class GitTestCase
{

	private GitRepository fRepo;
	private IPath fPath;

	@After
	public void tearDown() throws Exception
	{
		if (fRepo != null)
		{
			File generatedRepo = fRepo.workingDirectory().toFile();
			if (generatedRepo.exists())
			{
				delete(generatedRepo);
			}
			fRepo = null;
		}
		fPath = null;
	}

	protected synchronized IPath repoToGenerate() throws Exception
	{
		if (fPath == null)
		{
			fPath = FileUtil.getTempDirectory().append("git_repo" + System.currentTimeMillis());
		}
		return fPath;
	}

	protected synchronized GitRepository getRepo() throws Exception
	{
		if (fRepo == null)
		{
			fRepo = createRepo();
		}
		return fRepo;
	}

	protected GitRepository createRepo() throws Exception
	{
		return createRepo(repoToGenerate());
	}

	/**
	 * Create a git repo and make sure it actually generate a model object and not null
	 * 
	 * @param path
	 * @return
	 * @throws CoreException
	 */
	protected GitRepository createRepo(IPath path) throws CoreException
	{
		// FIXME Turn off a pref flag so we don't hook up the file watchers to git repo!
		getGitRepositoryManager().create(path);
		GitRepository repo = getGitRepositoryManager().getUnattachedExisting(path.toFile().toURI());
		assertNotNull(repo);
		// Remove the auto-generated .gitignore file!
		repo.workingDirectory().append(GitRepository.GITIGNORE).toFile().delete();
		return repo;
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	// Git specific assertions
	protected void assertCurrentBranch(String branchName)
	{
		assertEquals("Current branch is incorrect", branchName, fRepo.currentBranch());
	}

	protected void assertCommit(GitIndex index, String commitMessage) throws Exception
	{
		GitRepository repo = getRepo();
		try
		{
			repo.waitForWrite();
			IStatus status = index.commit(commitMessage);
			assertTrue("Failed to commit: " + status.getMessage(), status.isOK());
			assertTrue("After a commit, the repository changed file listing should be empty but is not", index
					.changedFiles().isEmpty());
		}
		finally
		{
			repo.exitWriteProcess();
		}
	}

	protected void assertUnstageFiles(GitIndex index, List<ChangedFile> changed) throws Exception
	{
		GitRepository repo = getRepo();
		try
		{
			repo.waitForWrite();
			IStatus status = index.unstageFiles(changed);
			assertTrue(MessageFormat.format("Failed to unstage changes: {0}", status.getMessage()), status.isOK());
		}
		finally
		{
			repo.exitWriteProcess();
		}
	}

	protected void assertStageFiles(GitIndex index, List<ChangedFile> changed) throws Exception
	{
		GitRepository repo = getRepo();
		try
		{
			repo.waitForWrite();
			IStatus status = index.stageFiles(changed);
			assertTrue(MessageFormat.format("Failed to stage changes: {0}", status.getMessage()), status.isOK());
		}
		finally
		{
			repo.exitWriteProcess();
		}
	}

	protected void assertModifiedUnstagedFile(ChangedFile changed)
	{
		assertUnstaged(changed);
		assertStatus(Status.MODIFIED, changed);
	}

	protected void assertModifiedStagedFile(ChangedFile changed)
	{
		assertStaged(changed);
		assertStatus(Status.MODIFIED, changed);
	}

	protected void assertDeletedUnstagedFile(ChangedFile changedFile)
	{
		assertUnstaged(changedFile);
		assertStatus(Status.DELETED, changedFile);
	}

	protected void assertDeletedStagedFile(ChangedFile changedFile)
	{
		assertStaged(changedFile);
		assertStatus(Status.DELETED, changedFile);
	}

	protected void assertNewStagedFile(ChangedFile changed)
	{
		assertStaged(changed);
		assertStatus(Status.NEW, changed);
	}

	protected void assertNewUnstagedFile(ChangedFile changed)
	{
		assertUnstaged(changed);
		assertStatus(Status.NEW, changed);
	}

	protected void assertStatus(Status status, ChangedFile file)
	{
		assertEquals("Changed file in git repo has unexpected status", status, file.getStatus());
	}

	/**
	 * Assert a changed file has staged changes and no unstaged changes.
	 * 
	 * @param file
	 */
	protected void assertStaged(ChangedFile file)
	{
		assertTrue("Changed file in git repo doesn't have expected staged changes", file.hasStagedChanges());
		assertFalse("Changed file in git repo has unexpected unstaged changes", file.hasUnstagedChanges());
	}

	/**
	 * Assert a changed file has unstaged changes and no staged changes.
	 * 
	 * @param file
	 */
	protected void assertUnstaged(ChangedFile file)
	{
		assertFalse("Changed file in git repo has unexpected staged changes", file.hasStagedChanges());
		assertTrue("Changed file in git repo doesn't have expected unstaged changes", file.hasUnstagedChanges());
	}

	/**
	 * Switch branch and make sure that it performed properly and update current branch in model.
	 * 
	 * @param branchName
	 */
	protected void assertSwitchBranch(String branchName)
	{
		IStatus status = fRepo.switchBranch(branchName, new NullProgressMonitor());
		assertTrue(MessageFormat.format("switchBranch returned an unexpected error status: {0}", status), status.isOK());
		assertCurrentBranch(branchName);
	}

	protected void assertCreateBranch(String newBranch, boolean track, String startPoint) throws Exception
	{
		assertTrue(MessageFormat.format("Failed to create new branch {0} off of {1} (track: {2})", newBranch,
				startPoint, track), getRepo().createBranch(newBranch, track, startPoint));
	}

	protected void assertRefresh() throws Exception
	{
		assertRefresh(getRepo().index());
	}

	protected void assertRefresh(GitIndex index)
	{
		IStatus status = index.refresh(new NullProgressMonitor());
		assertTrue(MessageFormat.format("Refreshing index returned error status: {0}", status), status.isOK());
	}

	/**
	 * Recursively delete a directory tree.
	 * 
	 * @param generatedRepo
	 */
	private void delete(File generatedRepo)
	{
		if (generatedRepo == null)
		{
			return;
		}
		File[] children = generatedRepo.listFiles();
		if (children != null)
		{
			for (File child : children)
			{
				delete(child);
			}
		}

		if (!generatedRepo.delete())
		{
			generatedRepo.deleteOnExit();
		}
	}
}
