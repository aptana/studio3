/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com.aptana.core.util.IOUtil;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitIndex;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevList;
import com.aptana.git.core.model.GitRevSpecifier;
import com.aptana.git.core.model.GitTestCase;

public class CommitFileRevisionTest extends GitTestCase
{

	@Test
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
		assertRefresh(index);

		// Stage the new file
		List<ChangedFile> toStage = index.changedFiles();
		assertStageFiles(index, toStage);
		assertRefresh(index);
		assertCommit(index, "Initial commit");

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

	@Test
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
			assertRefresh(index);

			// Stage and then commit the new file
			List<ChangedFile> toStage = index.changedFiles();
			assertStageFiles(index, toStage);
			assertRefresh(index);
			assertCommit(index, entry.getKey());
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
