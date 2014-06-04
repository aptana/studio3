/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitIndex;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitTestCase;
import com.aptana.testing.utils.ProjectCreator;

public class GitMoveDeleteIntegrationTest extends GitTestCase
{

	private static final String PROJECT_NAME = "gmdht"; //$NON-NLS-1$

	private IProject fProject;

	@Override
	public void tearDown() throws Exception
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

	@Override
	protected GitRepository createRepo(IPath path) throws CoreException
	{
		IEclipsePreferences prefs = new ProjectScope(getProject()).getNode(GitPlugin.PLUGIN_ID);
		prefs.putBoolean(IPreferenceConstants.REFRESH_INDEX_WHEN_RESOURCES_CHANGE, false);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			fail(e.getMessage());
		}

		GitRepository repo = super.createRepo(path);
		getGitRepositoryManager().createOrAttach(getProject(), new NullProgressMonitor());
		// delete auto-generated .gitignore file
		repo.workingDirectory().append(GitRepository.GITIGNORE).toFile().delete();
		return repo;
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

	@Test
	public void testDeleteNewUnstagedFile() throws Exception
	{
		IFile file = getProject().getFile("newfile.txt");
		file.create(new ByteArrayInputStream("Initial contents".getBytes()), true, new NullProgressMonitor());

		file.delete(IResource.NONE, new NullProgressMonitor());
		assertFalse(file.exists());
		// TODO Assert that we didn't delete through repo
	}

	@Test
	public void testDeleteStagedFile() throws Exception
	{
		IFile file = getProject().getFile("newfile2.txt");
		file.create(new ByteArrayInputStream("Initial contents".getBytes()), true, new NullProgressMonitor());

		GitIndex index = getRepo().index();
		assertRefresh(index);
		List<ChangedFile> changedFiles = index.changedFiles();
		assertContains("newfile2.txt", changedFiles);
		assertStageFiles(index, changedFiles);

		file.delete(IResource.NONE, new NullProgressMonitor());
		assertFalse(file.exists());
		// TODO Assert that we did delete through repo
	}

	protected void assertContains(String fileName, List<ChangedFile> changedFiles)
	{
		assertTrue("changed files was empty", changedFiles.size() > 0);
		for (ChangedFile file : changedFiles)
		{
			if (file.getRelativePath().equals(Path.fromPortableString(fileName)))
			{
				return;
			}
		}
		fail("Didn't find " + fileName);
	}

	@Test
	public void testDeleteAlreadyCommittedFileWithNoChanges() throws Exception
	{
		IFile file = getProject().getFile("newfile3.txt");
		file.create(new ByteArrayInputStream("Initial contents".getBytes()), true, new NullProgressMonitor());

		GitIndex index = getRepo().index();
		assertRefresh(index);
		List<ChangedFile> changedFiles = index.changedFiles();
		assertContains("newfile3.txt", changedFiles);
		assertStageFiles(index, changedFiles);

		assertCommit(index, "Initial commit");

		file.delete(IResource.NONE, new NullProgressMonitor());
		assertFalse(file.exists());
		// TODO Assert that we did delete through repo
	}

	@Test
	public void testDeleteUnstagedAlreadyCommittedFile() throws Exception
	{
		IFile file = getProject().getFile("newfile4.txt");
		file.create(new ByteArrayInputStream("Initial contents".getBytes()), true, new NullProgressMonitor());

		GitIndex index = getRepo().index();
		assertRefresh(index);
		List<ChangedFile> changedFiles = index.changedFiles();
		assertContains("newfile4.txt", changedFiles);
		assertStageFiles(index, changedFiles);

		assertCommit(index, "Initial commit");

		file.setContents(new ByteArrayInputStream("Modified contents".getBytes()), IResource.FORCE,
				new NullProgressMonitor());
		assertRefresh(index);

		file.delete(IResource.NONE, new NullProgressMonitor());
		assertFalse(file.exists());
		// TODO Assert that we did delete through repo
	}

	// public void testDeleteFolder()
	// {
	// fail("Not yet implemented.");
	// }
	//
	// public void testDeleteProject()
	// {
	// fail("Not yet implemented.");
	// }
	//
	// public void testMoveFile()
	// {
	// fail("Not yet implemented.");
	// }
	//
	// public void testMoveFolder()
	// {
	// fail("Not yet implemented.");
	// }
	//
	// public void testMoveProject()
	// {
	// fail("Not yet implemented.");
	// }

}
