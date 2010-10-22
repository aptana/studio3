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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.team.core.history.IFileHistoryProvider;
import org.eclipse.team.core.history.IFileRevision;

import com.aptana.core.util.IOUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitIndex;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.testing.utils.ProjectCreator;

public class GitFileHistoryTest extends TestCase
{

	private static final String PROJECT_NAME = "gfh_test"; //$NON-NLS-1$
	private IProject fProject;
	private GitRepository fRepo;

	public void testGetFileRevisions() throws Exception
	{
		GitRepository repo = getRepo();
		final String filename = "comitted_file.txt";

		List<String> commitsToMake = new ArrayList<String>();
		commitsToMake.add("Hello World!");
		commitsToMake.add("# Second commit contents.");

		GitIndex index = repo.index();
		// Actually add a file to the location
		IFile resource = getProject().getFile(filename);
		for (String contents : commitsToMake)
		{
			if (!resource.exists())
			{
				resource.create(new ByteArrayInputStream(contents.getBytes()), true, new NullProgressMonitor());
			}
			else
			{
				resource.setContents(new ByteArrayInputStream(contents.getBytes()), IResource.FORCE,
						new NullProgressMonitor());
			}
			// refresh the index
			index.refresh(new NullProgressMonitor());

			// Stage the new file
			int tries = 100;
			List<ChangedFile> toStage = index.changedFiles();
			// HACK Wait until we get a non-empty list?
			while (toStage == null || toStage.isEmpty())
			{
				Thread.sleep(50);
				toStage = index.changedFiles();
				tries--;
				if (tries <= 0)
					break;
			}
			assertNotNull(toStage);
			assertTrue(toStage.size() > 0);
			assertTrue(index.stageFiles(toStage));
			index.refresh(new NullProgressMonitor());
			assertTrue(index.commit(contents));
		}

		// Normal test
		GitFileHistory history = new GitFileHistory(resource, IFileHistoryProvider.NONE, new NullProgressMonitor());
		IFileRevision[] revs = history.getFileRevisions();
		assertNotNull(revs);
		assertEquals(2, revs.length);
		int i = revs.length - 1;
		for (IFileRevision revision : revs)
		{
			assertTrue(revision.exists());
			IStorage storage = revision.getStorage(new NullProgressMonitor());
			assertEquals(commitsToMake.get(i--), IOUtil.read(storage.getContents()));
			// Make sure getFileRevision works as we expect
			assertSame(revision, history.getFileRevision(revision.getContentIdentifier()));
		}

		// Test getContributors
		IFileRevision[] contributors = history.getContributors(revs[0]);
		assertNotNull(contributors);
		assertEquals(1, contributors.length);
		assertSame(contributors[0], revs[1]);

		// TODO Test when there are two+ contributors!

		// Test getTargets
		IFileRevision[] targets = history.getTargets(revs[1]);
		assertNotNull(targets);
		assertEquals(1, targets.length);
		assertSame(targets[0], revs[0]);

		// TODO Test when there are two+ targets!

		// Test with a flag for single revision!
		history = new GitFileHistory(resource, IFileHistoryProvider.SINGLE_REVISION, null);
		revs = history.getFileRevisions();
		assertNotNull(revs);
		assertEquals(1, revs.length);

		// TODO Test with single line of descent flag!
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		try
		{
			if (fProject != null)
				fProject.delete(true, new NullProgressMonitor());
		}
		finally
		{
			fProject = null;
			fRepo = null;
			super.tearDown();
		}
	}

	protected GitRepository getRepo() throws CoreException
	{
		if (fRepo == null)
		{
			fRepo = createRepo();
		}
		return fRepo;
	}

	protected GitRepository createRepo() throws CoreException
	{
		getGitRepositoryManager().create(getProject().getLocation());
		return getGitRepositoryManager().attachExisting(getProject(), new NullProgressMonitor());
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	protected IPath repoToGenerate() throws CoreException
	{
		return getProject().getLocation();
	}

	private IProject getProject() throws CoreException
	{
		if (fProject == null)
		{
			fProject = ProjectCreator.createAndOpen(PROJECT_NAME);
		}
		return fProject;
	}
}
