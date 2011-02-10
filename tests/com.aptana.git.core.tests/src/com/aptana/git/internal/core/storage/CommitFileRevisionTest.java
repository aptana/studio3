/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core.storage;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.aptana.core.util.IOUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitIndex;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevList;
import com.aptana.git.core.model.GitRevSpecifier;
import com.aptana.git.core.model.IGitRepositoryManager;

public class CommitFileRevisionTest extends TestCase
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
		getGitRepositoryManager().create(path);
		GitRepository repo = getGitRepositoryManager().getUnattachedExisting(path.toFile().toURI());
		assertNotNull(repo);
		fRepo = repo;
		return repo;
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	protected IPath repoToGenerate()
	{
		if (fPath == null)
			fPath = GitPlugin.getDefault().getStateLocation().append("git_cfr_" + System.currentTimeMillis());
		return fPath;
	}

	public void testCommitFileRevision() throws Exception
	{
		GitRepository repo = createRepo();

		final String filename = "comitted_file.txt";
		final String contents = "Hello World!";

		GitIndex index = repo.index();
		// Actually add a file to the location
		FileWriter writer = new FileWriter(repo.workingDirectory().append(filename).toFile());
		writer.write(contents);
		writer.close();
		// refresh the index
		index.refresh(new NullProgressMonitor());

		// Stage the new file
		List<ChangedFile> toStage = index.changedFiles();
		index.stageFiles(toStage);
		index.refresh(new NullProgressMonitor());
		index.commit("Initial commit");

		GitCommit gitCommit = new GitCommit(repo, "HEAD");
		CommitFileRevision revision = new CommitFileRevision(gitCommit, Path.fromPortableString(filename));
		assertTrue(revision.exists());
		assertEquals(filename, revision.getName());
		assertFalse(revision.isPropertyMissing());
		assertSame(revision, revision.withAllProperties(null));
		assertEquals(repo.workingDirectory().append(filename).toPortableString(), revision.getURI().getPath());
		IStorage storage = revision.getStorage(new NullProgressMonitor());
		assertEquals(contents, IOUtil.read(storage.getContents()));
	}

	public void testCommitFileRevisionMultipleCommits() throws Exception
	{
		GitRepository repo = createRepo();
		final String filename = "comitted_file.txt";

		Map<String, String> commitsToMake = new HashMap<String, String>();
		commitsToMake.put("Initial commit", "Hello World!");
		commitsToMake.put("Second commit", "# Second commit contents.");

		GitIndex index = repo.index();
		// Actually add a file to the location
		File txtFile = repo.workingDirectory().append(filename).toFile();
		for (Entry<String, String> entry : commitsToMake.entrySet())
		{
			FileWriter writer = new FileWriter(txtFile);
			writer.write(entry.getValue());
			writer.close();
			// refresh the index
			index.refresh(new NullProgressMonitor());

			// Stage the new file
			List<ChangedFile> toStage = index.changedFiles();
			index.stageFiles(toStage);
			index.refresh(new NullProgressMonitor());
			index.commit(entry.getKey());
		}

		GitRevList revList = new GitRevList(repo);
		revList.walkRevisionListWithSpecifier(new GitRevSpecifier(filename), -1, new NullProgressMonitor());
		List<GitCommit> commits = revList.getCommits();
		for (GitCommit commit : commits)
		{
			CommitFileRevision revision = new CommitFileRevision(commit, Path.fromPortableString(filename));
			assertTrue(revision.exists());
			assertEquals(commit.getAuthor(), revision.getAuthor());
			assertEquals(commit.getComment(), revision.getComment());
			assertEquals(commit.getTimestamp(), revision.getTimestamp());
			assertEquals(commit.sha(), revision.getContentIdentifier());
			IStorage storage = revision.getStorage(new NullProgressMonitor());
			assertEquals(commitsToMake.get(commit.getSubject()), IOUtil.read(storage.getContents()));
		}
	}
}
