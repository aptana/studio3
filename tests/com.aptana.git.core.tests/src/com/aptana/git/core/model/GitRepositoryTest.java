/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.aptana.core.util.IOUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.ChangedFile.Status;

@SuppressWarnings("nls")
public class GitRepositoryTest extends TestCase
{

	private GitRepository fRepo;
	private IPath fPath;

	@Override
	protected void tearDown() throws Exception
	{
		try
		{
			File generatedRepo = fRepo.workingDirectory().toFile();
			if (generatedRepo.exists())
			{
				delete(generatedRepo);
			}
			fRepo = null;
			fPath = null;
		}
		finally
		{
			super.tearDown();
		}
	}

	public void testCreate() throws Throwable
	{
		IPath path = repoToGenerate();
		// Doesn't yet exist
		GitRepository repo = getGitRepositoryManager().getUnattachedExisting(path.toFile().toURI());
		assertNull("Got a GitRepository instance, even though it doesn't exist yet!", repo);
		// Create it now and assert that it was created
		repo = createRepo(path);
		assertNotNull(repo);
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	public void testAddFileStageUnstageAndCommit() throws Throwable
	{
		GitRepository repo = createRepo();
		GitIndex index = repo.index();
		assertTrue(index.changedFiles().isEmpty());

		// Actually add a file to the location
		FileWriter writer = new FileWriter(fileToAdd());
		writer.write("Hello World!");
		writer.close();
		// refresh the index
		index.refresh(new NullProgressMonitor());

		// Now there should be a single file that's been changed!
		List<ChangedFile> changed = index.changedFiles();
		assertEquals("Repository changed file listing should contain one entry for the new file, but does not", 1,
				changed.size());

		// Make sure it's shown as having unstaged changes only and is NEW
		assertNewUnstagedFile(changed.get(0));

		// Stage the new file
		assertStageFiles(index, changed);
		assertNewStagedFile(changed.get(0));

		// Unstage the file
		assertUnstageFiles(index, changed);
		assertNewUnstagedFile(changed.get(0));

		// Stage
		assertStageFiles(index, changed);
		assertNewStagedFile(changed.get(0));

		// Commit
		assertCommit(index, "Initial commit");
	}

	public void testCommitMessageWithDoubleQuotes() throws Throwable
	{
		GitRepository repo = createRepo();
		GitIndex index = repo.index();
		assertTrue(index.changedFiles().isEmpty());

		// Actually add a file to the location
		FileWriter writer = new FileWriter(fileToAdd());
		writer.write("Hello World!");
		writer.close();
		// refresh the index
		index.refresh(new NullProgressMonitor());

		// Now there should be a single file that's been changed!
		List<ChangedFile> changed = index.changedFiles();
		assertEquals("Repository changed file listing should contain one entry for the new file, but does not", 1,
				changed.size());

		// Make sure it's shown as having unstaged changes only and is NEW
		assertNewUnstagedFile(changed.get(0));

		// stage
		assertStageFiles(index, changed);
		assertNewStagedFile(changed.get(0));

		// commit
		final String commitMessage = "Initial commit with \"double quotes\" inside the message!";
		assertCommit(index, commitMessage);

		// now grab the resulting log to see if the message escaped the quotes too many times!
		File file = repo.gitFile(GitRepository.COMMIT_EDITMSG);
		FileInputStream stream = null;
		try
		{
			stream = new FileInputStream(file);
			String result = IOUtil.read(stream);
			assertEquals(commitMessage + "\n", result);
		}
		finally
		{
			if (stream != null)
			{
				stream.close();
			}
		}
	}

	public void testDeleteFile() throws Throwable
	{
		testAddFileStageUnstageAndCommit();
		// Now delete the file we committed!
		File addedFile = new File(fileToAdd());
		// make sure it's there first
		assertTrue("File we want to delete through git repo doesn't exist", addedFile.exists());
		// delete it
		IStatus status = fRepo.deleteFile(addedFile.getName());
		assertTrue("Deleting file in git repo returned an error status", status.isOK());
		// make sure its deleted from filesystem
		assertFalse("Deleted file through git, file still exists", addedFile.exists());

		// Check the changed files and make sure it shows up as changed: DELETED, unstaged
		GitIndex index = fRepo.index();
		index.refresh(new NullProgressMonitor());

		// Now there should be a single file that's been changed!
		List<ChangedFile> changedFiles = index.changedFiles();
		assertEquals("Repository changed file listing should contain one entry for the deleted file, but does not", 1,
				changedFiles.size());

		// Make sure it's shown as having staged changes only and is DELETED
		assertDeletedStagedFile(changedFiles.get(0));

		// unstage
		assertUnstageFiles(index, changedFiles);
		assertDeletedUnstagedFile(changedFiles.get(0));

		// stage
		assertStageFiles(index, changedFiles);
		assertDeletedStagedFile(changedFiles.get(0));

		// commit
		assertCommit(index, "Delete files");
	}

	// Test modifying file that isn't new (already checked in)
	public void testModifyCheckedInFile() throws Throwable
	{
		testAddFileStageUnstageAndCommit();

		GitIndex index = fRepo.index();

		// Actually add a file to the location
		FileWriter writer = new FileWriter(fileToAdd(), true);
		writer.write("\nHello second line!");
		writer.close();
		// refresh the index
		index.refresh(new NullProgressMonitor());

		// Now there should be a single file that's been changed!
		List<ChangedFile> changed = index.changedFiles();
		assertEquals("Repository changed file listing should contain one entry for a new file, but does not", 1,
				changed.size());

		// Make sure it's shown as having unstaged changes only and is MODIFIED
		assertModifiedUnstagedFile(changed.get(0));

		// stage
		assertStageFiles(index, changed);
		assertModifiedStagedFile(changed.get(0));

		// unstage
		assertUnstageFiles(index, changed);
		assertModifiedUnstagedFile(changed.get(0));

		// stage
		assertStageFiles(index, changed);
		assertModifiedStagedFile(changed.get(0));

		// commit
		assertCommit(index, "Add second line");
	}

	public void testAddRemoveListeners() throws Throwable
	{
		final List<RepositoryEvent> eventsReceived = new ArrayList<RepositoryEvent>();
		IGitRepositoryListener listener = new IGitRepositoryListener()
		{
			public void indexChanged(IndexChangedEvent e)
			{
				eventsReceived.add(e);
			}

			public void branchChanged(BranchChangedEvent e)
			{
				eventsReceived.add(e);
			}

			public void pulled(PullEvent e)
			{
				eventsReceived.add(e);
			}

			public void branchAdded(BranchAddedEvent e)
			{
				eventsReceived.add(e);
			}

			public void branchRemoved(BranchRemovedEvent e)
			{
				eventsReceived.add(e);
			}

			public void pushed(PushEvent e)
			{
				eventsReceived.add(e);
			}
		};
		getRepo().addListener(listener);
		// TODO Attach and unattach repo with the RepositoryProvider and check those events

		testSwitchBranch();

		int size = eventsReceived.size();
		assertTrue("Expected git repo events, but got none", size > 0);
		assertBranchChangedEvent(new ArrayList<RepositoryEvent>(eventsReceived), "master", "my_new_branch");
		assertBranchChangedEvent(new ArrayList<RepositoryEvent>(eventsReceived), "my_new_branch", "master");

		fRepo.removeListener(listener);
		// Do some things that should send events and make sure we don't get any more.
		assertSwitchBranch("my_new_branch");
		assertEquals(size, eventsReceived.size());
	}

	protected GitRepository getRepo()
	{
		if (fRepo == null)
		{
			createRepo();
		}
		return fRepo;
	}

	protected void assertBranchChangedEvent(List<RepositoryEvent> events, String oldName, String newName)
	{
		for (RepositoryEvent event : events)
		{
			if (event instanceof BranchChangedEvent)
			{
				BranchChangedEvent branchChangeEvent = (BranchChangedEvent) event;
				if (branchChangeEvent.getOldBranchName().equals(oldName)
						&& branchChangeEvent.getNewBranchName().equals(newName))
				{
					return;
				}
			}
		}
		fail("No matching branch event");
	}

	// TODO Test deleting folder

	public void testAddBranch() throws Throwable
	{
		// Must be at least one file for us to be able to get branches and add them properly!
		testAddFileStageUnstageAndCommit();

		// Make sure we just have master branch
		Set<String> branches = fRepo.allBranches();
		assertEquals("Should only have one branch: " + branches.toString(), 1, branches.size());
		assertTrue(branches.contains("master"));

		// Create a new branch off master
		assertTrue(fRepo.createBranch("my_new_branch", false, "master"));

		// make sure the branch is listed in model
		branches = fRepo.allBranches();
		assertEquals("Should have one new branch: " + branches.toString(), 2, branches.size());
		assertTrue(branches.contains("master"));
		assertTrue(branches.contains("my_new_branch"));

		// TODO Add tests for creating tracking branches!
	}

	public void testDeleteBranch() throws Throwable
	{
		testAddBranch();

		// Delete the new branch
		assertTrue(fRepo.deleteBranch("my_new_branch").isOK());

		// make sure the branch is no longer listed in model
		Set<String> branches = fRepo.allBranches();
		assertEquals(1, branches.size());
		assertTrue(branches.contains("master"));
		assertFalse(branches.contains("my_new_branch"));
	}

	public void testSwitchBranch() throws Throwable
	{
		testAddBranch();

		assertCurrentBranch("master");
		assertSwitchBranch("my_new_branch");
		assertSwitchBranch("master");
	}

	public void testSwitchBranchClosesOpenProjectsThatDontExistOnDestinationBranch() throws Throwable
	{
		testAddBranch();

		assertCurrentBranch("master");
		assertSwitchBranch("my_new_branch");

		GitIndex index = fRepo.index();
		assertTrue("Expected changed file listing to be empty", index.changedFiles().isEmpty());

		// Create a new project on this branch!
		String projectName = "project_on_branch" + System.currentTimeMillis();

		File projectDir = fRepo.workingDirectory().append(projectName).toFile();
		projectDir.mkdirs();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(projectName);

		IProjectDescription description = workspace.newProjectDescription(projectName);
		description.setLocation(fRepo.workingDirectory().append(projectName));
		project.create(description, new NullProgressMonitor());

		// Commit the project on this branch!
		index.refresh(new NullProgressMonitor());

		// Now there should be a single file that's been changed!
		List<ChangedFile> changed = index.changedFiles();
		assertEquals("repository changed file listing should contain one entry for new .project file, but does not", 1,
				changed.size());

		// Make sure it's shown as having unstaged changes only and is NEW
		assertNewUnstagedFile(changed.get(0));

		// Stage the new file
		assertStageFiles(index, changed);
		assertNewStagedFile(changed.get(0));

		assertCommit(index, "Initial commit");

		assertSwitchBranch("master");

		// Assert that the new project is closed!
		project = workspace.getRoot().getProject(projectName);
		assertFalse(project.isOpen());

		// assert that there's no .project file stranded there
		File dotProject = new File(projectDir, IProjectDescription.DESCRIPTION_FILE_NAME);
		assertFalse(dotProject.exists());
	}

	public void testDeleteUnMergedBranch() throws Throwable
	{
		testAddBranch();

		assertSwitchBranch("my_new_branch");

		// Now we need to make changes, commit and then switch back to master
		GitIndex index = fRepo.index();

		// TODO Refactor out common code with testAddFileStageUnstageCommit
		// Actually add a file to the location
		String txtFile = fRepo.workingDirectory() + File.separator + "file_on_branch.txt";
		FileWriter writer = new FileWriter(txtFile);
		writer.write("Hello Branched World!");
		writer.close();
		// refresh the index
		index.refresh(new NullProgressMonitor());

		// Now there should be a single file that's been changed!
		List<ChangedFile> changedFiles = index.changedFiles();
		assertEquals(
				"repository changed file listing should contain one entry for a new file_on_branch.txt file, but does not",
				1, changedFiles.size());

		// Make sure it's shown as having unstaged changes only and is NEW
		assertNewUnstagedFile(changedFiles.get(0));

		// Stage the new file
		assertStageFiles(index, changedFiles);
		assertNewStagedFile(changedFiles.get(0));

		assertCommit(index, "Initial commit");

		// Now switch to master
		assertSwitchBranch("master");

		IStatus status = fRepo.deleteBranch("my_new_branch");
		assertFalse("Deleting an umerged branch didn't return an error status (as it should)", status.isOK());
		assertEquals(1, status.getCode());
		// Can't rely on the unmerged failure message from git to remain the same across versions.
		// assertEquals(
		// "error: The branch 'my_new_branch' is not an ancestor of your current HEAD.\nIf you are sure you want to delete it, run 'git branch -D my_new_branch'.",
		// status.getMessage());
	}

	public void testRemoteURLs() throws Exception
	{
		GitRepository repo = createRepo();
		File configFile = repoToGenerate().append(".git").append("config").toFile();
		// Test that our regexp can handle when fetch is first or url is first as child of remote section in config
		// file.
		FileWriter writer = new FileWriter(configFile, true);
		writer.append("\n[remote \"chris\"]\n\tfetch = +refs/heads/*:refs/remotes/origin/*\n\turl = git@github.com:aptana/chris.git\n[remote \"bob\"]\n\turl = git@github.com:aptana/bob.git\n\tfetch = +refs/heads/*:refs/remotes/origin/*\n");
		writer.close();
		Set<String> urls = repo.remoteURLs();
		assertEquals(2, urls.size());
		assertTrue(urls.contains("git@github.com:aptana/chris.git"));
		assertTrue(urls.contains("git@github.com:aptana/bob.git"));
	}

	public void testFirePullEvent()
	{
		GitRepository repo = createRepo();
		final List<PullEvent> pullEvents = new ArrayList<PullEvent>();
		repo.addListener(new AbstractGitRepositoryListener()
		{
			@Override
			public void pulled(PullEvent e)
			{
				pullEvents.add(e);
			}
		});
		assertTrue(pullEvents.isEmpty());
		repo.firePullEvent();
		assertEquals(1, pullEvents.size());
		assertSame(repo, pullEvents.get(0).getRepository());
	}

	public void testFirePushEvent()
	{
		GitRepository repo = createRepo();
		final List<PushEvent> pushEvents = new ArrayList<PushEvent>();
		repo.addListener(new AbstractGitRepositoryListener()
		{
			@Override
			public void pushed(PushEvent e)
			{
				pushEvents.add(e);
			}
		});
		assertTrue(pushEvents.isEmpty());
		repo.firePushEvent();
		assertEquals(1, pushEvents.size());
		assertSame(repo, pushEvents.get(0).getRepository());
	}

	public void testDontBlockToAcquireLocks() throws Exception
	{
		// Force a write operation on repo, then while it is running, ask for a read operation
		// make sure we never deadlock, but fail gracefully if we can't acquire the lock!
		final GitRepository repo = createRepo();
		final Random r = new Random();
		repo.enterWriteProcess();
		try
		{
			final boolean[] finished = new boolean[1];
			Thread t2 = new Thread(new Runnable()
			{

				public void run()
				{
					for (int i = 0; i < 100; i++)
					{
						boolean acquired = repo.enterRead();
						try
						{
							Thread.sleep(r.nextInt(10));
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
							return;
						}
						finally
						{
							if (acquired)
							{
								repo.exitRead();
							}
						}
					}
					finished[0] = true;
				}
			});
			t2.start();
			t2.join(1500);
			t2.interrupt();

			assertTrue("Failed to finish read lock acquiring thread, may be blocked", finished[0]);
		}
		finally
		{
			repo.exitWriteProcess();
		}
	}

	protected IPath repoToGenerate()
	{
		if (fPath == null)
		{
			String tmpDirString = System.getProperty("java.io.tmpdir");
			fPath = new Path(tmpDirString).append("git_repo" + System.currentTimeMillis());
			// fPath = GitPlugin.getDefault().getStateLocation().append("git_repo" + System.currentTimeMillis());
		}
		return fPath;
	}

	protected String fileToAdd()
	{
		return fRepo.workingDirectory() + File.separator + "file.txt";
	}

	protected GitRepository createRepo()
	{
		return createRepo(repoToGenerate());
	}

	/**
	 * Create a git repo and make sure it actually generate a model object and not null
	 * 
	 * @param path
	 * @return
	 */
	protected GitRepository createRepo(IPath path)
	{
		// FIXME Turn off a pref flag so we don't hook up the file watchers to git repo!
		getGitRepositoryManager().create(path);
		GitRepository repo = getGitRepositoryManager().getUnattachedExisting(path.toFile().toURI());
		assertNotNull(repo);
		fRepo = repo;
		// Remove the auto-generated .gitignore file!
		fRepo.workingDirectory().append(GitRepository.GITIGNORE).toFile().delete();
		return repo;
	}

	// Git specific assertions
	protected void assertCurrentBranch(String branchName)
	{
		assertEquals("Current branch is incorrect", branchName, fRepo.currentBranch());
	}

	protected void assertCommit(GitIndex index, String commitMessage)
	{
		assertTrue("Failed to commit", index.commit(commitMessage));
		assertTrue("After a commit, the repository changed file listing should be empty but is not", index
				.changedFiles().isEmpty());
	}

	protected void assertUnstageFiles(GitIndex index, List<ChangedFile> changed)
	{
		assertTrue("Failed to unstage changes", index.unstageFiles(changed));
	}

	protected void assertStageFiles(GitIndex index, List<ChangedFile> changed)
	{
		assertTrue("Failed to stage changes", index.stageFiles(changed));
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
		assertTrue("switchBranch returned an unexpected error status", status.isOK());
		assertCurrentBranch(branchName);
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
