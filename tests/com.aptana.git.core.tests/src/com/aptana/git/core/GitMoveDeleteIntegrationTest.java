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
package com.aptana.git.core;

import java.io.ByteArrayInputStream;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitIndex;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.testing.utils.ProjectCreator;

public class GitMoveDeleteIntegrationTest extends TestCase
{

	private static final String PROJECT_NAME = "gmdht"; //$NON-NLS-1$

	private IProject project;
	private GitRepository repo;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		project = ProjectCreator.createAndOpen(PROJECT_NAME);

		// create a git repo
		getGitRepositoryManager().create(project.getLocation());
		repo = getGitRepositoryManager().attachExisting(project, new NullProgressMonitor());
		// delete auto-generated .gitignore file
		repo.workingDirectory().append(GitRepository.GITIGNORE).toFile().delete();
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	@Override
	protected void tearDown() throws Exception
	{
		try
		{
			if (project != null)
				project.delete(true, new NullProgressMonitor());
		}
		finally
		{
			project = null;
			repo = null;
			super.tearDown();
		}
	}

	public void testDeleteNewUnstagedFile() throws Exception
	{
		IFile file = project.getFile("newfile.txt");
		file.create(new ByteArrayInputStream("Initial contents".getBytes()), true, new NullProgressMonitor());

		file.delete(IResource.NONE, new NullProgressMonitor());
		assertFalse(file.exists());
		// TODO Assert that we didn't delete through repo
	}

	public void testDeleteStagedFile() throws Exception
	{
		IFile file = project.getFile("newfile2.txt");
		file.create(new ByteArrayInputStream("Initial contents".getBytes()), true, new NullProgressMonitor());

		GitIndex index = repo.index();
		index.refresh(new NullProgressMonitor());
		List<ChangedFile> changedFiles = index.changedFiles();
		assertEquals(2, changedFiles.size());
		repo.index().stageFiles(changedFiles);

		file.delete(IResource.NONE, new NullProgressMonitor());
		assertFalse(file.exists());
		// TODO Assert that we did delete through repo
	}

	public void testDeleteAlreadyCommittedFileWithNoChanges() throws Exception
	{
		IFile file = project.getFile("newfile3.txt");
		file.create(new ByteArrayInputStream("Initial contents".getBytes()), true, new NullProgressMonitor());

		GitIndex index = repo.index();
		index.refresh(new NullProgressMonitor());
		List<ChangedFile> changedFiles = index.changedFiles();
		assertEquals(2, changedFiles.size());
		repo.index().stageFiles(changedFiles);

		repo.index().commit("Initial commit");

		file.delete(IResource.NONE, new NullProgressMonitor());
		assertFalse(file.exists());
		// TODO Assert that we did delete through repo
	}

	public void testDeleteUnstagedAlreadyCommittedFile() throws Exception
	{
		IFile file = project.getFile("newfile4.txt");
		file.create(new ByteArrayInputStream("Initial contents".getBytes()), true, new NullProgressMonitor());

		GitIndex index = repo.index();
		index.refresh(new NullProgressMonitor());
		List<ChangedFile> changedFiles = index.changedFiles();
		assertEquals(2, changedFiles.size());
		repo.index().stageFiles(changedFiles);

		repo.index().commit("Initial commit");

		file.setContents(new ByteArrayInputStream("Modified contents".getBytes()), IResource.FORCE,
				new NullProgressMonitor());
		repo.index().refresh(new NullProgressMonitor());

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
