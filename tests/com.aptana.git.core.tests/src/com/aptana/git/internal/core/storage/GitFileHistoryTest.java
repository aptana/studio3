/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core.storage;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.team.core.history.IFileHistoryProvider;
import org.eclipse.team.core.history.IFileRevision;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitIndex;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitTestCase;
import com.aptana.testing.utils.ProjectCreator;

public class GitFileHistoryTest extends GitTestCase
{

	private static final String PROJECT_NAME = "gfh_test"; //$NON-NLS-1$
	private IProject fProject;

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
			while (CollectionsUtil.isEmpty(toStage))
			{
				Thread.sleep(50);
				toStage = index.changedFiles();
				tries--;
				if (tries <= 0)
				{
					break;
				}
			}
			assertNotNull("Expected a non-null list of changes to stage", toStage);
			assertTrue("Expected at least one change to stage, but there are none", toStage.size() > 0);

			assertStageFiles(index, toStage);

			index.refresh(new NullProgressMonitor());

			assertCommit(index, contents);
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
	protected void tearDown() throws Exception
	{
		try
		{
			if (fProject != null)
			{
				fProject.delete(true, new NullProgressMonitor());
			}
		}
		finally
		{
			fProject = null;
			super.tearDown();
		}
	}

	protected GitRepository createRepo() throws CoreException
	{
		getGitRepositoryManager().create(getProject().getLocation());
		return getGitRepositoryManager().attachExisting(getProject(), new NullProgressMonitor());
	}

	protected IPath repoToGenerate() throws CoreException
	{
		return getProject().getLocation();
	}

	private synchronized IProject getProject() throws CoreException
	{
		if (fProject == null)
		{
			fProject = ProjectCreator.createAndOpen(PROJECT_NAME);
		}
		return fProject;
	}
}
