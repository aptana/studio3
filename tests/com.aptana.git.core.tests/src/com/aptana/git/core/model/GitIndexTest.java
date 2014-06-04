package com.aptana.git.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.model.ChangedFile.Status;

public class GitIndexTest extends GitTestCase
{
	@Test
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
		ChangedFile changedFile = ChangedFile.createInstance(fileName, Status.UNMERGED);
		changedFile.hasUnstagedChanges = true;
		blah.add(changedFile);
		GitIndex index = new GitIndex(repo);
		index.changedFiles = blah;

		assertTrue(index.hasUnresolvedMergeConflicts());

		List<ChangedFile> filesToStage = index.changedFiles();
		assertNotNull(filesToStage);
		assertEquals(1, filesToStage.size());
		ChangedFile fileToStage = filesToStage.iterator().next();
		assertTrue(fileToStage.hasUnmergedChanges());
		assertTrue(fileToStage.hasUnstagedChanges());

		// Now stage the "fix"
		assertStageFiles(index, filesToStage);

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

	@Test
	public void testUnstageFilesUpdatesStagedFlagsOnAffectedFiles() throws Exception
	{
		GitRepository repo = createRepo();
		String fileName = "somefile.txt";

		// Actually add a file to the location
		FileWriter writer = new FileWriter(repo.workingDirectory().append(fileName).toOSString());
		writer.write("Hello World!");
		writer.close();

		GitIndex index = new GitIndex(repo);

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
		assertUnstageFiles(index, filesToUnstage);

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

	@Test
	public void testBatchRefreshRepoWithNoCommitsAndNewUnstagedFile() throws Exception
	{
		GitRepository repo = createRepo();
		String fileName = "somefile.txt";
		// Actually add a file to the location
		FileWriter writer = new FileWriter(repo.workingDirectory().append(fileName).toOSString());
		writer.write("Hello World!");
		writer.close();

		assertRefresh();
		List<ChangedFile> files = repo.index().changedFiles();
		assertContains(files, fileName, Status.NEW, false, true);
	}

	// FIXME There seems to be no way to tell if an untracked file is staged or unstaged...?
	// @Test
	// public void testBatchRefreshRepoWithNoCommitsAndNewStagedFile() throws Exception
	// {
	// testBatchRefreshRepoWithNoCommitsAndNewUnstagedFile();
	// GitIndex index = getRepo().index();
	//
	// // stage the new file
	// List<ChangedFile> files = index.changedFiles();
	// assertTrue(files.size() >= 1);
	// assertStageFiles(index, files);
	//
	// // Batch Refresh
	// assertRefresh();
	// files = index.changedFiles();
	// assertContains(files, "somefile.txt", Status.NEW, true, false);
	// }

	@Test
	public void testBatchRefreshRepoWithEveryStatus() throws Exception
	{
		GitRepository repo = createRepo();
		// Actually add a file to the location
		FileWriter writer = new FileWriter(repo.workingDirectory().append("file1.txt").toOSString());
		writer.write("Hello World!");
		writer.close();

		writer = new FileWriter(repo.workingDirectory().append("file2.txt").toOSString());
		writer.write("Hello World!");
		writer.close();

		writer = new FileWriter(repo.workingDirectory().append("file3.txt").toOSString());
		writer.write("Hello World!");
		writer.close();

		writer = new FileWriter(repo.workingDirectory().append("file4.txt").toOSString());
		writer.write("Hello World!");
		writer.close();

		// Stage and commit the files
		assertStageFiles(repo.index(), repo.index().changedFiles());
		assertCommit(repo.index(), "initial");

		// Commit a couple files, then test staged delete, unstaged delete, untracked, staged mod, unstaged mod.

		// Delete 1 and 2
		repo.deleteFile("file1.txt");
		repo.deleteFile("file2.txt");

		// Modify 3 and 4
		writer = new FileWriter(repo.workingDirectory().append("file3.txt").toOSString(), true);
		writer.write("\nAdded line");
		writer.close();

		writer = new FileWriter(repo.workingDirectory().append("file4.txt").toOSString(), true);
		writer.write("\nAdded line");
		writer.close();

		// Create 5 and 6
		writer = new FileWriter(repo.workingDirectory().append("file5.txt").toOSString());
		writer.write("Untracked file");
		writer.close();

		writer = new FileWriter(repo.workingDirectory().append("file6.txt").toOSString());
		writer.write("Untracked file 6");
		writer.close();

		assertRefresh();

		// Stage 1, 3, and 5
		List<ChangedFile> files = repo.index().changedFiles();
		List<ChangedFile> toStage = CollectionsUtil.filter(files, new IFilter<ChangedFile>()
		{
			public boolean include(ChangedFile item)
			{
				return CollectionsUtil.newSet("file1.txt", "file3.txt", "file5.txt").contains(item.portablePath);
			}
		});
		assertStageFiles(repo.index(), toStage);

		// Explicitly unstage 2
		List<ChangedFile> toUnstage = CollectionsUtil.filter(files, new IFilter<ChangedFile>()
		{
			public boolean include(ChangedFile item)
			{
				return CollectionsUtil.newSet("file2.txt").contains(item.portablePath);
			}
		});
		assertUnstageFiles(repo.index(), toUnstage);

		// refresh and make sure all files have correct status in changed file listing
		assertRefresh();
		files = repo.index().changedFiles();

		assertContains(files, "file1.txt", Status.DELETED, true, false);
		assertContains(files, "file2.txt", Status.DELETED, false, true);
		assertContains(files, "file3.txt", Status.MODIFIED, true, false);
		assertContains(files, "file4.txt", Status.MODIFIED, false, true);
		assertContains(files, "file5.txt", Status.NEW, true, false);
		assertContains(files, "file6.txt", Status.NEW, false, true);
	}

	@Test
	public void testDiffRefreshRepoWithEveryStatus() throws Exception
	{
		GitRepository repo = createRepo();
		// Actually add a file to the location
		FileWriter writer = new FileWriter(repo.workingDirectory().append("file1.txt").toOSString());
		writer.write("Hello World!");
		writer.close();

		writer = new FileWriter(repo.workingDirectory().append("file2.txt").toOSString());
		writer.write("Hello World!");
		writer.close();

		writer = new FileWriter(repo.workingDirectory().append("file3.txt").toOSString());
		writer.write("Hello World!");
		writer.close();

		writer = new FileWriter(repo.workingDirectory().append("file4.txt").toOSString());
		writer.write("Hello World!");
		writer.close();

		// Stage and commit the files
		assertStageFiles(repo.index(), repo.index().changedFiles());
		assertCommit(repo.index(), "initial");

		// Commit a couple files, then test staged delete, unstaged delete, untracked, staged mod, unstaged mod.

		// Delete 1 and 2
		repo.deleteFile("file1.txt");
		repo.deleteFile("file2.txt");

		// Modify 3 and 4
		writer = new FileWriter(repo.workingDirectory().append("file3.txt").toOSString(), true);
		writer.write("\nAdded line");
		writer.close();

		writer = new FileWriter(repo.workingDirectory().append("file4.txt").toOSString(), true);
		writer.write("\nAdded line");
		writer.close();

		// Create 5 and 6
		writer = new FileWriter(repo.workingDirectory().append("file5.txt").toOSString());
		writer.write("Untracked file");
		writer.close();

		writer = new FileWriter(repo.workingDirectory().append("file6.txt").toOSString());
		writer.write("Untracked file 6");
		writer.close();

		assertRefresh();

		// Stage 1, 3, and 5
		List<ChangedFile> files = repo.index().changedFiles();
		List<ChangedFile> toStage = CollectionsUtil.filter(files, new IFilter<ChangedFile>()
		{
			public boolean include(ChangedFile item)
			{
				return CollectionsUtil.newSet("file1.txt", "file3.txt", "file5.txt").contains(item.portablePath);
			}
		});
		assertStageFiles(repo.index(), toStage);

		// Explicitly unstage 2
		List<ChangedFile> toUnstage = CollectionsUtil.filter(files, new IFilter<ChangedFile>()
		{
			public boolean include(ChangedFile item)
			{
				return CollectionsUtil.newSet("file2.txt").contains(item.portablePath);
			}
		});
		assertUnstageFiles(repo.index(), toUnstage);

		// refresh and make sure all files have correct status in changed file listing
		Set<IPath> filePaths = CollectionsUtil.newSet(Path.fromPortableString("file1.txt"),
				Path.fromPortableString("file2.txt"), Path.fromPortableString("file3.txt"),
				Path.fromPortableString("file4.txt"), Path.fromPortableString("file5.txt"),
				Path.fromPortableString("file6.txt"));
		repo.index().refresh(false, filePaths, null);
		files = repo.index().changedFiles();

		assertContains(files, "file1.txt", Status.DELETED, true, false);
		assertContains(files, "file2.txt", Status.DELETED, false, true);
		assertContains(files, "file3.txt", Status.MODIFIED, true, false);
		assertContains(files, "file4.txt", Status.MODIFIED, false, true);
		assertContains(files, "file5.txt", Status.NEW, true, false);
		assertContains(files, "file6.txt", Status.NEW, false, true);
	}

	private void assertContains(List<ChangedFile> files, final String path, final Status status,
			final boolean hasStaged, final boolean hasUnstaged)
	{
		List<ChangedFile> matching = CollectionsUtil.filter(files, new IFilter<ChangedFile>()
		{

			public boolean include(ChangedFile item)
			{
				return ObjectUtil.areEqual(item.portablePath, path) && item.hasStagedChanges == hasStaged
						&& item.hasUnstagedChanges == hasUnstaged && item.status == status;
			}
		});
		List<String> fileStrings = CollectionsUtil.map(files, new IMap<ChangedFile, String>()
		{

			public String map(ChangedFile item)
			{
				return item.toString();
			}

		});
		assertFalse(MessageFormat.format(
				"Unable to find listing for file with name {0}, status {1}, staged? {2}, unstaged? {3} in:\n{4}", path,
				status, hasStaged, hasUnstaged, StringUtil.join(", ", fileStrings)), CollectionsUtil.isEmpty(matching));
	}

	@Test
	public void testDeadlock() throws Exception
	{
		final GitRepository repo = getRepo();
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
				index.refresh(true, null, monitor);
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
