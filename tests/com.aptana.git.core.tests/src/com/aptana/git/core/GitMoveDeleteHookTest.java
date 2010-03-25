package com.aptana.git.core;

import java.io.ByteArrayInputStream;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitIndex;
import com.aptana.git.core.model.GitRepository;

public class GitMoveDeleteHookTest extends TestCase
{

	private IProject project;
	private GitRepository repo;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		project = ResourcesPlugin.getWorkspace().getRoot().getProject("gmdht");
		if (!project.exists())
			project.create(new NullProgressMonitor());
		if (!project.isOpen())
			project.open(new NullProgressMonitor());

		// create a git repo
		GitRepository.create(project.getLocation().toOSString());
		repo = GitRepository.attachExisting(project, new NullProgressMonitor());
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
		index.refresh();
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
		index.refresh();
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
		index.refresh();
		List<ChangedFile> changedFiles = index.changedFiles();
		assertEquals(2, changedFiles.size());
		repo.index().stageFiles(changedFiles);

		repo.index().commit("Initial commit");

		file.setContents(new ByteArrayInputStream("Modified contents".getBytes()), IResource.FORCE,
				new NullProgressMonitor());
		repo.index().refresh();

		file.delete(IResource.NONE, new NullProgressMonitor());
		assertFalse(file.exists());
		// TODO Assert that we did delete through repo
	}

	public void testDeleteFolder()
	{
		fail("Not yet implemented.");
	}

	public void testDeleteProject()
	{
		fail("Not yet implemented.");
	}

	public void testMoveFile()
	{
		fail("Not yet implemented.");
	}

	public void testMoveFolder()
	{
		fail("Not yet implemented.");
	}

	public void testMoveProject()
	{
		fail("Not yet implemented.");
	}

}
