/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
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

	/**
	 * Recursively delete a directory tree.
	 * 
	 * @param generatedRepo
	 */
	private void delete(File generatedRepo)
	{
		if (generatedRepo == null)
			return;
		File[] children = generatedRepo.listFiles();
		if (children != null)
		{
			for (File child : children)
			{
				delete(child);
			}
		}

		if (!generatedRepo.delete())
			generatedRepo.deleteOnExit();
	}

	public void testCreate() throws Throwable
	{
		IPath path = repoToGenerate();
		// Doesn't yet exist
		GitRepository repo = getGitRepositoryManager().getUnattachedExisting(path.toFile().toURI());
		assertNull(repo);
		// Create it now and assert that it was created
		createRepo(path);
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
		assertFalse(changed.isEmpty());
		assertEquals(1, changed.size());

		// Make sure it's shown as having unstaged changes only and is NEW
		assertUnstaged(changed.get(0));
		assertStatus(Status.NEW, changed.get(0));

		// Stage the new file
		assertFalse(changed.isEmpty());
		assertTrue(index.stageFiles(changed));
		assertStaged(changed.get(0));
		assertStatus(Status.NEW, changed.get(0));

		// Unstage the file
		assertFalse(changed.isEmpty());
		assertTrue(index.unstageFiles(changed));
		assertUnstaged(changed.get(0));
		assertStatus(Status.NEW, changed.get(0));

		// stage again so we can commit...
		assertFalse(changed.isEmpty());
		assertTrue(index.stageFiles(changed));
		assertStaged(changed.get(0));
		assertStatus(Status.NEW, changed.get(0));

		index.commit("Initial commit");
		// No more changed files now...
		assertTrue(index.changedFiles().isEmpty());
	}

	public void testDeleteFile() throws Throwable
	{
		testAddFileStageUnstageAndCommit();
		// Now delete the file we committed!
		File addedFile = new File(fileToAdd());
		// make sure it's there first
		assertTrue(addedFile.exists());
		// delete it
		IStatus status = fRepo.deleteFile(addedFile.getName());
		assertTrue(status.isOK());
		// make sure its deleted from filesystem
		assertFalse(addedFile.exists());

		// Check the changed files and make sure it shows up as changed: DELETED, unstaged
		GitIndex index = fRepo.index();
		index.refresh(new NullProgressMonitor());

		// Now there should be a single file that's been changed!
		List<ChangedFile> changedFiles = index.changedFiles();
		assertFalse(changedFiles.isEmpty());
		assertEquals(1, changedFiles.size());

		// Make sure it's shown as having staged changes only and is DELETED
		assertStaged(changedFiles.get(0));
		assertStatus(Status.DELETED, changedFiles.get(0));

		// Unstage the file
		assertTrue(index.unstageFiles(changedFiles));
		assertUnstaged(changedFiles.get(0));
		assertStatus(Status.DELETED, changedFiles.get(0));

		// stage again so we can commit...
		assertTrue(index.stageFiles(changedFiles));
		assertStaged(changedFiles.get(0));
		assertStatus(Status.DELETED, changedFiles.get(0));

		index.commit("Delete files");
		// No more changed files now...
		assertTrue(index.changedFiles().isEmpty());
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
		assertFalse(changed.isEmpty());
		assertEquals(1, changed.size());

		// Make sure it's shown as having unstaged changes only and is MODIFIED
		assertUnstaged(changed.get(0));
		assertStatus(Status.MODIFIED, changed.get(0));

		// Stage the new file
		assertTrue(index.stageFiles(changed));
		assertStaged(changed.get(0));
		assertStatus(Status.MODIFIED, changed.get(0));

		// Unstage the file
		assertTrue(index.unstageFiles(changed));
		assertUnstaged(changed.get(0));
		assertStatus(Status.MODIFIED, changed.get(0));

		// stage again so we can commit...
		assertTrue(index.stageFiles(changed));
		assertStaged(changed.get(0));
		assertStatus(Status.MODIFIED, changed.get(0));

		index.commit("Add second line");
		// No more changed files now...
		assertTrue(index.changedFiles().isEmpty());
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
		assertTrue(size > 0);
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
			createRepo();
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

		assertEquals("master", fRepo.currentBranch());
		assertSwitchBranch("my_new_branch");
		assertSwitchBranch("master");
	}

	public void testSwitchBranchClosesOpenProjectsThatDontExistOnDestinationBranch() throws Throwable
	{
		testAddBranch();

		assertEquals("master", fRepo.currentBranch());
		assertSwitchBranch("my_new_branch");

		GitIndex index = fRepo.index();
		assertTrue(index.changedFiles().isEmpty());

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
		assertFalse(changed.isEmpty());
		assertEquals(1, changed.size());

		// Make sure it's shown as having unstaged changes only and is NEW
		assertUnstaged(changed.get(0));
		assertStatus(Status.NEW, changed.get(0));

		// Stage the new file
		assertFalse(changed.isEmpty());
		assertTrue(index.stageFiles(changed));
		assertStaged(changed.get(0));
		assertStatus(Status.NEW, changed.get(0));

		index.commit("Initial commit");
		// No more changed files now...
		assertTrue(index.changedFiles().isEmpty());

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
		assertFalse(changedFiles.isEmpty());
		assertEquals(1, changedFiles.size());

		// Make sure it's shown as having unstaged changes only and is NEW
		assertUnstaged(changedFiles.get(0));
		assertStatus(Status.NEW, changedFiles.get(0));

		// Stage the new file
		assertTrue(index.stageFiles(changedFiles));
		assertStaged(changedFiles.get(0));
		assertStatus(Status.NEW, changedFiles.get(0));

		index.commit("Initial commit");
		// No more changed files now...
		assertTrue(index.changedFiles().isEmpty());

		// Now switch to master
		assertSwitchBranch("master");

		IStatus status = fRepo.deleteBranch("my_new_branch");
		assertFalse(status.isOK());
		assertEquals(1, status.getCode());
		// Can't rely on the unmerged failure message from git to remain the same across versions.
		// assertEquals(
		// "error: The branch 'my_new_branch' is not an ancestor of your current HEAD.\nIf you are sure you want to delete it, run 'git branch -D my_new_branch'.",
		// status.getMessage());
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

	private void assertStatus(Status status, ChangedFile file)
	{
		assertEquals(status, file.getStatus());
	}

	/**
	 * Assert a changed file has staged changes and no unstaged changes.
	 * 
	 * @param file
	 */
	protected void assertStaged(ChangedFile file)
	{
		assertTrue(file.hasStagedChanges());
		assertFalse(file.hasUnstagedChanges());
	}

	/**
	 * Assert a changed file has unstaged changes and no staged changes.
	 * 
	 * @param file
	 */
	protected void assertUnstaged(ChangedFile file)
	{
		assertFalse(file.hasStagedChanges());
		assertTrue(file.hasUnstagedChanges());
	}

	/**
	 * Switch branch and make sure that it performed properly and update current branch in model.
	 * 
	 * @param branchName
	 */
	protected void assertSwitchBranch(String branchName)
	{
		assertTrue(fRepo.switchBranch(branchName, new NullProgressMonitor()));
		assertEquals(branchName, fRepo.currentBranch());
	}
}
