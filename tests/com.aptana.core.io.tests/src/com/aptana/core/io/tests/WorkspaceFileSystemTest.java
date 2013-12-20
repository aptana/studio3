/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.io.tests;

import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.io.efs.WorkspaceFileSystem;
import com.aptana.core.util.FileUtil;

@SuppressWarnings("nls")
public class WorkspaceFileSystemTest
{

	private IProject project;

//	@Override
	@After
	public void tearDown() throws Exception
	{
		try
		{
			if (project != null)
			{
				try
				{
					project.refreshLocal(IResource.DEPTH_INFINITE, null);
				}
				catch (Exception e)
				{
					// ignore
				}
				try
				{
					project.delete(true, null);
				}
				catch (Exception e)
				{
					// ignore
				}
			}
		}
		finally
		{
			project = null;
//			super.tearDown();
		}
	}

	@Test
	public void testGetInstance()
	{
		assertNotNull(WorkspaceFileSystem.getInstance());
	}

	@Test
	public void testAttributes()
	{
		assertEquals("Workspace FS attributes should match Local FS", EFS.getLocalFileSystem().attributes(),
				WorkspaceFileSystem.getInstance().attributes());
	}

	@Test
	public void testCanDelete() throws IOException
	{
		assertTrue(WorkspaceFileSystem.getInstance().canDelete());
	}

	@Test
	public void testCanWrite() throws IOException
	{
		assertTrue(WorkspaceFileSystem.getInstance().canWrite());
	}

	@Test
	public void testIsCaseSensitive()
	{
		assertEquals("Workspace FS case-sensitivity should match Local FS", EFS.getLocalFileSystem().isCaseSensitive(),
				WorkspaceFileSystem.getInstance().isCaseSensitive());
	}

	@Test
	public void testNonWorkspaceLocalFile() throws IOException
	{
		File tempFile = File.createTempFile("test", ".txt").getCanonicalFile();
		try
		{
			assertNull("Non-workspace local file cannot be converted to workspace file store", WorkspaceFileSystem
					.getInstance().fromLocalFile(tempFile));
		}
		finally
		{
			assertTrue(tempFile.delete());
		}
	}

	@Test
	public void testWorkspaceLocalFile() throws IOException, CoreException, InvocationTargetException,
			InterruptedException
	{
		File tempDir = File.createTempFile("project", null).getCanonicalFile();
		try
		{
			assertTrue(tempDir.delete());
			assertTrue(tempDir.mkdirs());
			IProject project = createProject(tempDir);

			File file = File.createTempFile("text", ".txt", tempDir);
			project.refreshLocal(IProject.DEPTH_INFINITE, null);
			assertEquals("Make sure it's the same file", file, project.getFile(file.getName()).getLocation().toFile());

			IFileStore fs = WorkspaceFileSystem.getInstance().fromLocalFile(file);
			assertNotNull("Local file residing in workspace can be converted to workspace file store", fs);
			assertEquals("Returned file store should belong to workspace FS", WorkspaceFileSystem.getInstance(),
					fs.getFileSystem());
			assertEquals("Workspace file store convertion to file should match local file is was created from", file,
					fs.toLocalFile(EFS.NONE, null));
		}
		finally
		{
			FileUtil.deleteRecursively(tempDir);
		}
	}

	@Test
	public void testGetFileStoreByPath() throws IOException, CoreException, InvocationTargetException,
			InterruptedException
	{
		File tempDir = File.createTempFile("project", null).getCanonicalFile();
		try
		{
			assertTrue(tempDir.delete());
			assertTrue(tempDir.mkdirs());
			IProject project = createProject(tempDir);

			File file = File.createTempFile("text", ".txt", tempDir);
			project.refreshLocal(IProject.DEPTH_INFINITE, null);
			assertEquals("Make sure it's the same file", file, project.getFile(file.getName()).getLocation().toFile());

			IPath path = project.getFile(file.getName()).getFullPath();
			IFileStore fs = WorkspaceFileSystem.getInstance().getStore(path);
			assertTrue("File does not exist", fs.fetchInfo().exists());
			assertEquals("Workspace and local file are not the same", file, fs.toLocalFile(EFS.NONE, null));
			URI uri = fs.toURI();
			assertNotNull("toURI returned null", uri);
			assertEquals("URI scheme doesn't match", "workspace", uri.getScheme());
			assertEquals("URI path doesn't match", path.toPortableString(), uri.getPath());

		}
		finally
		{
			FileUtil.deleteRecursively(tempDir);
		}
	}

	@Test
	public void testGetFileStoreByURI() throws IOException, CoreException, InvocationTargetException,
			InterruptedException, URISyntaxException
	{
		File tempDir = File.createTempFile("project", null).getCanonicalFile();
		try
		{
			assertTrue(tempDir.delete());
			assertTrue(tempDir.mkdirs());
			IProject project = createProject(tempDir);

			File file = File.createTempFile("text", ".txt", tempDir);
			project.refreshLocal(IProject.DEPTH_INFINITE, null);
			assertEquals("Make sure it's the same file", file, project.getFile(file.getName()).getLocation().toFile());

			IPath path = project.getFile(file.getName()).getFullPath();
			URI targetURI = new URI(WorkspaceFileSystem.getInstance().getScheme(), path.toPortableString(), null);
			IFileStore fs = WorkspaceFileSystem.getInstance().getStore(targetURI);
			assertTrue("File does not exist", fs.fetchInfo().exists());
			assertEquals("Workspace and local file are not the same", file, fs.toLocalFile(EFS.NONE, null));
			URI uri = fs.toURI();
			assertNotNull("toURI returned null", uri);
			assertEquals("URI does not match", targetURI, uri);

		}
		finally
		{
			FileUtil.deleteRecursively(tempDir);
		}
	}

	@Test
	public void testGetNonExistingFileStoreByPath()
	{
		IPath path = Path.fromPortableString("/nonexistingProject/nonexistingFile.txt");
		IFileStore fs = WorkspaceFileSystem.getInstance().getStore(path);
		assertNotNull("File store should not be null", fs);
		assertFalse("File does not exists", fs.fetchInfo().exists());
		assertEquals("Path doesn't match", path.toPortableString(), fs.toURI().getPath());
	}

	@Test
	public void testGetNonExistingFileStoreByURI() throws URISyntaxException
	{
		URI uri = new URI(WorkspaceFileSystem.getInstance().getScheme(), "/nonexistingProject/nonexistingFile.txt",
				null);
		IFileStore fs = WorkspaceFileSystem.getInstance().getStore(uri);
		assertNotNull("File store should be not null", fs);
		assertFalse("File does not exists", fs.fetchInfo().exists());
		assertEquals("URI doesn't match", uri, fs.toURI());
	}

	@Test
	public void testPathWithSpecialCharacters()
	{
		IFileStore fs = WorkspaceFileSystem.getInstance().getStore(
				Path.fromPortableString("/Test _Site/file [2008-09-21].php"));
		URI uri = fs.toURI();
		assertNotNull("URI should not be null", uri);
		assertEquals("File store should match when fetched by URI", fs, WorkspaceFileSystem.getInstance().getStore(uri));
	}

	/**
	 * Creates a project for testing
	 * 
	 * @return
	 * @throws IOException
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 * @throws CoreException
	 */
	private IProject createProject(File projectFolder) throws IOException, InvocationTargetException,
			InterruptedException, CoreException
	{
		String projectName = getClass().getSimpleName() + System.currentTimeMillis();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription description = workspace.newProjectDescription(projectName);
		description.setLocation(Path.fromOSString(projectFolder.getAbsolutePath()));

		IProject project = workspace.getRoot().getProject(projectName);
		project.create(description, null);
		this.project = project;

		project.open(null);
		assertTrue(project.isOpen());

		return project;
	}

}
