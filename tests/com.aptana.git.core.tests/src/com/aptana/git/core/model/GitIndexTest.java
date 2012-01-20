package com.aptana.git.core.model;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.ChangedFile.Status;

public class GitIndexTest extends TestCase
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

	public void testStageFilesUpdatesStagedFlagsOnAffectedFiles() throws Exception
	{
		GitRepository repo = createRepo();
		String fileName = "somefile.txt";

		// Actually add a file to the location
		FileWriter writer = new FileWriter(repo.workingDirectory().append(fileName).toOSString());
		writer.write("Hello World!");
		writer.close();

		// Generate faked unmerged file in index
		List<ChangedFile> blah = new ArrayList<ChangedFile>();
		ChangedFile changedFile = new ChangedFile(fileName, Status.UNMERGED);
		changedFile.hasUnstagedChanges = true;
		blah.add(changedFile);
		GitIndex index = new GitIndex(repo, repo.workingDirectory());
		index.changedFiles = blah;

		assertTrue(index.hasUnresolvedMergeConflicts());

		List<ChangedFile> filesToStage = index.changedFiles();
		assertNotNull(filesToStage);
		assertEquals(1, filesToStage.size());
		ChangedFile fileToStage = filesToStage.iterator().next();
		assertTrue(fileToStage.hasUnmergedChanges());
		assertTrue(fileToStage.hasUnstagedChanges());

		// Now stage the "fix"
		assertTrue("Staging file failed", index.stageFiles(filesToStage));

		// Now that the fix is staged, there should be no "merge conflict"
		assertFalse(index.hasUnresolvedMergeConflicts());

		// Now make sure that the changed files list inside the index has the updated status/flags
		List<ChangedFile> postStageFiles = index.changedFiles();
		assertNotNull(postStageFiles);
		assertEquals(1, postStageFiles.size());
		ChangedFile file = postStageFiles.iterator().next();
		assertTrue("Didn't update the staged status of file inside GitIndex's changedFiles list after staging",
				file.hasStagedChanges());
		assertFalse("Didn't update the unstaged status of file inside GitIndex's changedFiles list after staging",
				file.hasUnstagedChanges());

		// Passed in arg also is updated
		assertTrue("Didn't update the staged status of file inside passed-in argument to stageFiles",
				fileToStage.hasStagedChanges());
		assertFalse("Didn't update the unstaged status of file inside passed-in argument to stageFiles",
				fileToStage.hasUnstagedChanges());
	}

	public void testUnstageFilesUpdatesStagedFlagsOnAffectedFiles() throws Exception
	{
		GitRepository repo = createRepo();
		String fileName = "somefile.txt";

		// Actually add a file to the location
		FileWriter writer = new FileWriter(repo.workingDirectory().append(fileName).toOSString());
		writer.write("Hello World!");
		writer.close();

		GitIndex index = new GitIndex(repo, repo.workingDirectory());

		// Commit the new file
		index.stageFiles(index.changedFiles());
		index.commit("initial commit");

		// Now edit the file...
		writer = new FileWriter(repo.workingDirectory().append(fileName).toOSString(), true);
		writer.write(" It's me again!");
		writer.close();
		// stage it
		index.refresh(null);
		index.stageFiles(index.changedFiles());

		// Now fake the new status as being unmerged
		List<ChangedFile> blah = index.changedFiles();
		blah.iterator().next().status = Status.UNMERGED;
		index.changedFiles = blah;

		assertFalse(index.hasUnresolvedMergeConflicts());

		List<ChangedFile> filesToUnstage = index.changedFiles();
		assertNotNull(filesToUnstage);
		assertEquals(1, filesToUnstage.size());
		ChangedFile fileToStage = filesToUnstage.iterator().next();
		assertTrue(fileToStage.hasUnmergedChanges());
		assertTrue(fileToStage.hasStagedChanges());

		// Now unstage the "fix"
		assertTrue("Unstaging file failed", index.unstageFiles(filesToUnstage));

		assertTrue(index.hasUnresolvedMergeConflicts());

		// Now make sure that the changed files list inside the index has the updated status/flags
		List<ChangedFile> postUnstageFiles = index.changedFiles();
		assertNotNull(postUnstageFiles);
		assertEquals(1, postUnstageFiles.size());
		ChangedFile file = postUnstageFiles.iterator().next();
		assertFalse("Didn't update the staged status of file inside GitIndex's changedFiles list after unstaging",
				file.hasStagedChanges());
		assertTrue("Didn't update the unstaged status of file inside GitIndex's changedFiles list after unstaging",
				file.hasUnstagedChanges());

		// Passed in arg also is updated
		assertFalse("Didn't update the staged status of file inside passed-in argument to unstageFiles",
				fileToStage.hasStagedChanges());
		assertTrue("Didn't update the unstaged status of file inside passed-in argument to unstageFiles",
				fileToStage.hasUnstagedChanges());
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

	protected IPath repoToGenerate()
	{
		if (fPath == null)
		{
			String tmpDirString = System.getProperty("java.io.tmpdir");
			fPath = new Path(tmpDirString).append("git_repo" + System.currentTimeMillis());
		}
		return fPath;
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

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	public void testDeadlock() throws Exception
	{
		final GitRepository repo = createRepo();
		final GitIndex index = repo.index();
		final Object notifier = new Object();
		final boolean[] finished = new boolean[2];
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				IProgressMonitor monitor = new NullProgressMonitor();
				synchronized (notifier)
				{
					notifier.notify();
				}
				index.refresh(true, monitor);
				finished[0] = true;
			}
		});
		Thread t2 = new Thread(new Runnable()
		{

			public void run()
			{
				try
				{
					synchronized (notifier)
					{
						notifier.wait();
					}
					Thread.sleep(5);
					List<ChangedFile> changedFiles = index.changedFiles();
					assertNotNull(changedFiles);
					finished[1] = true;
				}
				catch (InterruptedException e)
				{
					fail("Failed!");
				}
			}
		});

		t2.start();
		t.start();
		// Now give them some time to finish, up to 5 seconds each thread.
		t.join(5000);
		t2.join(5000);
		// if they haven't finished, forcibly interrupt them
		t.interrupt();
		t2.interrupt();
		// Now check to see if the calls ever finished normally, or the interrupt killed them
		assertTrue("Call to refresh() never finished normally", finished[0]);
		assertTrue("Call to changedFiles() never finished normally", finished[1]);
	}
}
