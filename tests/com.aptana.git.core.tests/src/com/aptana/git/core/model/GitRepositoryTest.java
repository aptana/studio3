package com.aptana.git.core.model;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
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
			IPath path = new Path(fRepo.workingDirectory());
			File generatedRepo = path.toFile();
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
		GitRepository repo = GitRepository.getUnattachedExisting(path.toFile().toURI());
		assertNull(repo);
		// Create it now and assert that it was created
		createRepo(path);
	}

	public void testAddFileStageUnstageAndCommit() throws Throwable
	{
		GitRepository repo = createRepo();
		GitIndex index = repo.index();
		assertTrue(index.changedFiles().isEmpty());

		// Actually add a file to the location
		String txtFile = fileToAdd();
		FileWriter writer = new FileWriter(txtFile);
		writer.write("Hello World!");
		writer.close();
		// refresh the index
		index.refresh();

		// Now there should be a single file that's been changed!
		assertFalse(index.changedFiles().isEmpty());
		assertEquals(1, index.changedFiles().size());

		// Make sure it's shown as having unstaged changes only and is NEW
		assertUnstaged(index.changedFiles().get(0));
		assertStatus(Status.NEW, index.changedFiles().get(0));

		// Stage the new file
		assertTrue(index.stageFiles(index.changedFiles()));
		assertStaged(index.changedFiles().get(0));
		assertStatus(Status.NEW, index.changedFiles().get(0));

		// Unstage the file
		assertTrue(index.unstageFiles(index.changedFiles()));
		assertUnstaged(index.changedFiles().get(0));
		assertStatus(Status.NEW, index.changedFiles().get(0));

		// stage again so we can commit...
		assertTrue(index.stageFiles(index.changedFiles()));
		assertStaged(index.changedFiles().get(0));
		assertStatus(Status.NEW, index.changedFiles().get(0));

		index.commit("Initial commit");
		// No more changed files now...
		assertTrue(index.changedFiles().isEmpty());
	}

	public void testDeleteFile() throws Throwable
	{
		testAddFileStageUnstageAndCommit();
		// Now delete the file we committed!
		String addedFile = fileToAdd();
		// make sure it's there first
		assertTrue(new File(addedFile).exists());
		// delete it
		assertTrue(fRepo.deleteFile(addedFile));
		// make sure its deleted from filesystem
		assertFalse(new File(addedFile).exists());

		// Check the changed files and make sure it shows up as changed: DELETED, unstaged
		GitIndex index = fRepo.index();

		// Now there should be a single file that's been changed!
		assertFalse(index.changedFiles().isEmpty());
		assertEquals(1, index.changedFiles().size());

		// Make sure it's shown as having unstaged changes only and is MODIFIED
		assertStaged(index.changedFiles().get(0));
		assertStatus(Status.DELETED, index.changedFiles().get(0));

		// Unstage the file
		assertTrue(index.unstageFiles(index.changedFiles()));
		assertUnstaged(index.changedFiles().get(0));
		assertStatus(Status.DELETED, index.changedFiles().get(0));

		// stage again so we can commit...
		assertTrue(index.stageFiles(index.changedFiles()));
		assertStaged(index.changedFiles().get(0));
		assertStatus(Status.DELETED, index.changedFiles().get(0));

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
		String txtFile = fileToAdd();
		FileWriter writer = new FileWriter(txtFile, true);
		writer.write("\nHello second line!");
		writer.close();
		// refresh the index
		index.refresh();

		// Now there should be a single file that's been changed!
		assertFalse(index.changedFiles().isEmpty());
		assertEquals(1, index.changedFiles().size());

		// Make sure it's shown as having unstaged changes only and is MODIFIED
		assertUnstaged(index.changedFiles().get(0));
		assertStatus(Status.MODIFIED, index.changedFiles().get(0));

		// Stage the new file
		assertTrue(index.stageFiles(index.changedFiles()));
		assertStaged(index.changedFiles().get(0));
		assertStatus(Status.MODIFIED, index.changedFiles().get(0));

		// Unstage the file
		assertTrue(index.unstageFiles(index.changedFiles()));
		assertUnstaged(index.changedFiles().get(0));
		assertStatus(Status.MODIFIED, index.changedFiles().get(0));

		// stage again so we can commit...
		assertTrue(index.stageFiles(index.changedFiles()));
		assertStaged(index.changedFiles().get(0));
		assertStatus(Status.MODIFIED, index.changedFiles().get(0));

		index.commit("Add second line");
		// No more changed files now...
		assertTrue(index.changedFiles().isEmpty());
	}

	public void testAddRemoveListeners() throws Throwable
	{
		final List<RepositoryEvent> eventsReceived = new ArrayList<RepositoryEvent>();
		IGitRepositoryListener listener = new IGitRepositoryListener()
		{
			@Override
			public void repositoryRemoved(RepositoryRemovedEvent e)
			{
				eventsReceived.add(e);
			}

			@Override
			public void repositoryAdded(RepositoryAddedEvent e)
			{
				eventsReceived.add(e);
			}

			@Override
			public void indexChanged(IndexChangedEvent e)
			{
				eventsReceived.add(e);
			}

			@Override
			public void branchChanged(BranchChangedEvent e)
			{
				eventsReceived.add(e);
			}
		};
		GitRepository.addListener(listener);
		// TODO Attach and unattach repo with the RepositoryProvider and check those events

		testSwitchBranch();

		int size = eventsReceived.size();
		assertTrue(size > 0);
		assertBranchChangedEvent(eventsReceived, "master", "my_new_branch");
		assertBranchChangedEvent(eventsReceived, "my_new_branch", "master");

		GitRepository.removeListener(listener);
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
				if (branchChangeEvent.getOldBranchName().equals(oldName) && branchChangeEvent.getNewBranchName().equals(newName))
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
		assertEquals(1, branches.size());
		assertTrue(branches.contains("master"));

		// Create a new branch off master
		assertTrue(fRepo.createBranch("my_new_branch", false, "master"));

		// make sure the branch is listed in model
		branches = fRepo.allBranches();
		assertEquals(2, branches.size());
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
		index.refresh();

		// Now there should be a single file that's been changed!
		assertFalse(index.changedFiles().isEmpty());
		assertEquals(1, index.changedFiles().size());

		// Make sure it's shown as having unstaged changes only and is NEW
		assertUnstaged(index.changedFiles().get(0));
		assertStatus(Status.NEW, index.changedFiles().get(0));

		// Stage the new file
		assertTrue(index.stageFiles(index.changedFiles()));
		assertStaged(index.changedFiles().get(0));
		assertStatus(Status.NEW, index.changedFiles().get(0));

		index.commit("Initial commit");
		// No more changed files now...
		assertTrue(index.changedFiles().isEmpty());

		// Now switch to master
		assertSwitchBranch("master");

		IStatus status = fRepo.deleteBranch("my_new_branch");
		assertFalse(status.isOK());
		assertEquals(1, status.getCode());
		assertEquals(
				"error: The branch 'my_new_branch' is not an ancestor of your current HEAD.\nIf you are sure you want to delete it, run 'git branch -D my_new_branch'.",
				status.getMessage());
	}

	protected IPath repoToGenerate()
	{
		if (fPath == null)
			fPath = GitPlugin.getDefault().getStateLocation().append("git_repo" + System.currentTimeMillis());
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
		GitRepository.create(path.toOSString());
		GitRepository repo = GitRepository.getUnattachedExisting(path.toFile().toURI());
		assertNotNull(repo);
		fRepo = repo;
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
		assertTrue(fRepo.switchBranch(branchName));
		assertEquals(branchName, fRepo.currentBranch());
	}
}
