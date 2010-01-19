package com.aptana.git.core.model;

import java.io.File;
import java.io.FileWriter;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
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
		// TODO Verify that we sent correct args to git executable!
	}

	// TODO Test modifying file that isn't new (already checked in)
	// TODO Test adding/removing listeners and receiving events!
	// TODO Test deleting folder
	// TODO Test changing branches and checking currentBranch

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

		// Create a new branch off master
		assertTrue(fRepo.deleteBranch("my_new_branch").isOK());

		// make sure the branch is no longer listed in model
		Set<String> branches = fRepo.allBranches();
		assertEquals(1, branches.size());
		assertTrue(branches.contains("master"));
		assertFalse(branches.contains("my_new_branch"));
		// TODO Try to delete a branch that won't work and needs to be run with -D!
	}

	public void testChangeBranch() throws Throwable
	{
		testAddBranch();

		assertEquals("master", fRepo.currentBranch());
		fRepo.switchBranch("my_new_branch");
		assertEquals("my_new_branch", fRepo.currentBranch());

		fRepo.switchBranch("master");
		assertEquals("master", fRepo.currentBranch());
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

	protected void assertStaged(ChangedFile file)
	{
		assertTrue(file.hasStagedChanges());
		assertFalse(file.hasUnstagedChanges());
	}

	protected void assertUnstaged(ChangedFile file)
	{
		assertFalse(file.hasStagedChanges());
		assertTrue(file.hasUnstagedChanges());
	}
}
