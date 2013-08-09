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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.osgi.framework.Version;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.IOUtil;

@SuppressWarnings("nls")
public class GitRepositoryTest extends GitTestCase
{

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

	public void testRepoRelativePath() throws Throwable
	{
		IProject project = null;
		try
		{
			GitRepository repo = createRepo();
			IPath repoPath = repo.workingDirectory();

			String projectName = repoPath.lastSegment();

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProjectDescription description = workspace.newProjectDescription(projectName);
			description.setLocation(repoPath);

			project = workspace.getRoot().getProject(projectName);
			project.create(description, null);
			project.open(null);

			IPath relativePath = repo.relativePath(project);
			assertTrue("Expected relative path of root of repo to be empty", relativePath.isEmpty());
		}
		finally
		{
			if (project != null)
			{
				project.delete(true, null);
			}
		}
	}

	public void testAddFileStageUnstageAndCommit() throws Exception
	{
		GitRepository repo = createRepo();
		GitIndex index = repo.index();
		assertTrue(index.changedFiles().isEmpty());

		// Actually add a file to the location
		FileWriter writer = new FileWriter(fileToAdd());
		writer.write("Hello World!");
		writer.close();
		// refresh the index
		assertRefresh();

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

	public void testMultipleLineCommitMessage() throws Throwable
	{
		GitRepository repo = createRepo();
		GitIndex index = repo.index();
		assertTrue(index.changedFiles().isEmpty());

		// Actually add a file to the location
		FileWriter writer = new FileWriter(fileToAdd());
		writer.write("Hello World!");
		writer.close();
		// refresh the index
		assertRefresh();

		// Now there should be a single file that's been changed!
		List<ChangedFile> changed = index.changedFiles();
		assertEquals("Repository changed file listing should contain one entry for the new file, but does not", 1,
				changed.size());

		// Hold onto filename/path for getting it's history later.
		ChangedFile file = changed.get(0);
		String filePath = file.getPath();

		// stage
		assertStageFiles(index, changed);
		assertNewStagedFile(changed.get(0));

		// commit
		final String commitMessage = "Subject of the commit.\n  - Did something\n  - did something else\n";
		assertCommit(index, commitMessage);

		GitRevList list = new GitRevList(repo);
		IStatus result = list.walkRevisionListWithSpecifier(new GitRevSpecifier(filePath), new NullProgressMonitor());
		assertTrue(result.isOK());

		List<GitCommit> commits = list.getCommits();
		assertEquals("commit list size", 1, commits.size());
		GitCommit commit = commits.get(0);

		Version v = GitExecutable.instance().version();
		if (v.compareTo(Version.parseVersion("1.7.3")) < 0)
		{
			assertEquals("subject", "Subject of the commit.   - Did something   - did something else",
					commit.getSubject());
		}
		else
		{
			assertEquals("subject", "Subject of the commit.", commit.getSubject());
			assertEquals("comment", commitMessage, commit.getComment());
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
		IStatus status = getRepo().deleteFile(addedFile.getName());
		assertTrue(MessageFormat.format("Deleting file in git repo returned an error status: {0}", status),
				status.isOK());
		// make sure its deleted from filesystem
		assertFalse("Deleted file through git, file still exists", addedFile.exists());

		// Check the changed files and make sure it shows up as changed: DELETED, unstaged
		GitIndex index = getRepo().index();
		assertRefresh(index);

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

		GitIndex index = getRepo().index();

		// Actually add a file to the location
		FileWriter writer = new FileWriter(fileToAdd(), true);
		writer.write("\nHello second line!");
		writer.close();
		// refresh the index
		assertRefresh();

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

		getRepo().removeListener(listener);
		// Do some things that should send events and make sure we don't get any more.
		assertSwitchBranch("my_new_branch");
		assertEquals(size, eventsReceived.size());
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
		Set<String> branches = getRepo().allBranches();
		assertEquals("Should only have one branch: " + branches.toString(), 1, branches.size());
		assertTrue(branches.contains("master"));

		// Create a new branch off master
		assertCreateBranch("my_new_branch", false, "master");

		// make sure the branch is listed in model
		branches = getRepo().allBranches();
		assertEquals("Should have one new branch: " + branches.toString(), 2, branches.size());
		assertTrue(branches.contains("master"));
		assertTrue(branches.contains("my_new_branch"));

		// TODO Add tests for creating tracking branches!
	}

	public void testDeleteBranch() throws Throwable
	{
		testAddBranch();

		// Delete the new branch
		assertTrue(getRepo().deleteBranch("my_new_branch").isOK());

		// make sure the branch is no longer listed in model
		Set<String> branches = getRepo().allBranches();
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

		GitIndex index = getRepo().index();
		assertTrue("Expected changed file listing to be empty", index.changedFiles().isEmpty());

		// Create a new project on this branch!
		String projectName = "project_on_branch" + System.currentTimeMillis();

		File projectDir = getRepo().workingDirectory().append(projectName).toFile();
		projectDir.mkdirs();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(projectName);

		IProjectDescription description = workspace.newProjectDescription(projectName);
		description.setLocation(getRepo().workingDirectory().append(projectName));
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
		GitIndex index = getRepo().index();

		// TODO Refactor out common code with testAddFileStageUnstageCommit
		// Actually add a file to the location
		String txtFile = getRepo().workingDirectory() + File.separator + "file_on_branch.txt";
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

		IStatus status = getRepo().deleteBranch("my_new_branch");
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
		File configFile = repoToGenerate().append(GitRepository.GIT_DIR).append(GitRepository.CONFIG_FILENAME).toFile();
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

	public void testMatchingRemoteBranchWithTrackedBranch() throws Exception
	{
		// Must be at least one file for us to be able to get branches and add them properly!
		testAddFileStageUnstageAndCommit();
		GitRepository repo = getRepo();

		assertCurrentBranch("master");
		assertNull("Expected to get no matching remote branch for 'master', but did",
				repo.matchingRemoteBranch("master"));

		// @formatter:off
		String configContents =
				"[remote \"origin\"]\n" +
				"\tfetch = +refs/heads/*:refs/remotes/origin/*\n" +
				"\turl = git@github.com:aptana/origin.git\n" +
				"[remote \"upstream\"]\n" +
				"\turl = git@github.com:aptana/upstream.git\n" +
				"\tfetch = +refs/heads/*:refs/remotes/upstream/*\n" +
				"[branch \"master\"]\n" +
		        "\tremote = origin\n" +
		        "\tmerge = refs/heads/master\n" +
		        "\trebase = true\n";
        // @formatter:on

		// Set up remotes
		File configFile = repo.workingDirectory().append(GitRepository.GIT_DIR).append(GitRepository.CONFIG_FILENAME)
				.toFile();
		FileWriter writer = new FileWriter(configFile, true);
		writer.append(configContents);
		writer.close();

		assertEquals("Expected to get matching remote branch for 'master'",
				GitRef.refFromString(GitRef.REFS_REMOTES + "origin/master"), repo.matchingRemoteBranch("master"));
	}

	public void testMatchingRemoteBranchWithImplicitlyTrackedBranch() throws Exception
	{
		// Must be at least one file for us to be able to get branches and add them properly!
		testAddFileStageUnstageAndCommit();
		GitRepository repo = getRepo();

		assertCurrentBranch("master");
		assertNull("Expected to get no matching remote branch for 'master', but did",
				repo.matchingRemoteBranch("master"));

		// Grab HEAD SHA
		File masterSHA = repo.workingDirectory().append(GitRepository.GIT_DIR).append("refs").append("heads")
				.append("master").toFile();
		String sha = IOUtil.read(new FileInputStream(masterSHA));

		// Write that SHA to the remotes/origin/master ref
		File ref = repo.workingDirectory().append(GitRepository.GIT_DIR).append("refs").append("remotes")
				.append("origin").append("master").toFile();
		ref.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(ref, true);
		writer.append(sha);
		writer.close();
		// Force reload of refs
		repo.hasChanged();
		repo.lazyReload();

		// Now make sure we try implicit tracking
		assertEquals("Expected to get matching remote branch for 'master'",
				GitRef.refFromString(GitRef.REFS_REMOTES + "origin/master"), repo.matchingRemoteBranch("master"));
	}

	public void testFirePullEvent() throws Exception
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

	public void testFirePushEvent() throws Exception
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

	public void testRemoveRemote() throws Throwable
	{
		// Generate remotes
		testRemoteURLs();

		IStatus status = getRepo().removeRemote("chris");
		assertTrue(status.isOK());

		// Now verify that 'chris' remote isn't in model
		Set<String> remoteNames = getRepo().remotes();
		assertEquals(1, remoteNames.size());
		assertTrue(remoteNames.contains("bob"));

		// Nor is it's URL endpoint
		Set<String> urls = getRepo().remoteURLs();
		assertEquals(1, urls.size());
		assertTrue(urls.contains("git@github.com:aptana/bob.git"));
	}

	public void testAddRemote() throws Throwable
	{
		// Generate remotes
		testRemoteURLs();

		IStatus status = getRepo().addRemote("newRemote", "git@github.com:user/newRemote.git", false);
		assertTrue(status.isOK());

		Set<String> remoteNames = getRepo().remotes();
		assertEquals(3, remoteNames.size());
		assertTrue(remoteNames.contains("newRemote"));

		Set<String> urls = getRepo().remoteURLs();
		assertEquals(3, urls.size());
		assertTrue(urls.contains("git@github.com:user/newRemote.git"));
	}

	protected String fileToAdd() throws Exception
	{
		return getRepo().workingDirectory() + File.separator + "file.txt";
	}

	public void testSSHGithubURL() throws Exception
	{
		GitRepository repo = new GitRepository(repoToGenerate().toFile().toURI())
		{
			@Override
			public Map<String, String> remotePairs() throws CoreException
			{
				return CollectionsUtil.newMap(GitRepository.ORIGIN, "git@github.com:appcelerator/titanium_studio.git");
			}
		};

		assertEquals("appcelerator/titanium_studio", repo.getGithubRepoName());
	}

	public void testHTTPSGithubURL() throws Exception
	{
		GitRepository repo = new GitRepository(repoToGenerate().toFile().toURI())
		{
			@Override
			public Map<String, String> remotePairs() throws CoreException
			{
				return CollectionsUtil.newMap(GitRepository.ORIGIN,
						"https://github.com/appcelerator/titanium_studio.git");
			}
		};
		assertEquals("appcelerator/titanium_studio", repo.getGithubRepoName());
	}

	public void testPeriodInRepoName() throws Exception
	{
		GitRepository repo = new GitRepository(repoToGenerate().toFile().toURI())
		{
			@Override
			public Map<String, String> remotePairs() throws CoreException
			{
				return CollectionsUtil.newMap(GitRepository.ORIGIN, "git@github.com:aptana/html.ruble.git");
			}
		};
		assertEquals("aptana/html.ruble", repo.getGithubRepoName());
	}

	public void testDeprecatedGitReadOnlyGithubURL() throws Exception
	{
		GitRepository repo = new GitRepository(repoToGenerate().toFile().toURI())
		{
			@Override
			public Map<String, String> remotePairs() throws CoreException
			{
				return CollectionsUtil.newMap(GitRepository.ORIGIN, "git://github.com/user/repo.git");
			}
		};
		assertEquals("user/repo", repo.getGithubRepoName());
	}
}
