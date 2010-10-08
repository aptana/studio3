/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
		CommitFileRevision revision = new CommitFileRevision(gitCommit, filename);
		assertTrue(revision.exists());
		assertEquals(filename, revision.getName());
		assertFalse(revision.isPropertyMissing());
		assertSame(revision, revision.withAllProperties(null));
		assertEquals(filename, revision.getURI().getPath());
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
			CommitFileRevision revision = new CommitFileRevision(commit, filename);
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
