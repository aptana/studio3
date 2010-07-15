package com.aptana.core.io.tests;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.ide.core.io.efs.EFSUtils;

import junit.framework.TestCase;

public class EFSUtilsTest extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testGetAbsolutePath() throws IOException, CoreException
	{
		File f = File.createTempFile("test", "txt");
		LocalConnectionPoint lcp = new LocalConnectionPoint(Path.fromOSString(f.getAbsolutePath()));
		assertEquals(f.getAbsolutePath(), EFSUtils.getAbsolutePath(lcp.getRoot()));
	}

	public void testGetPath() throws IOException, CoreException
	{
		File f = File.createTempFile("test", "txt");
		LocalConnectionPoint lcp = new LocalConnectionPoint(Path.fromOSString(f.getAbsolutePath()));
		assertEquals(f.getAbsolutePath(), EFSUtils.getAbsolutePath(lcp.getRoot()));
	}

	public void testGetRelativePath() throws IOException, CoreException
	{
		File f = File.createTempFile("test", "txt");
		File f2 = File.createTempFile("test", "txt");
		assertEquals("/" + f.getName(), EFSUtils.getRelativePath(EFS.getLocalFileSystem().fromLocalFile(
				f.getParentFile()), EFS.getLocalFileSystem().fromLocalFile(f)));

		assertEquals("", EFSUtils.getRelativePath(EFS.getLocalFileSystem().fromLocalFile(f.getParentFile()), EFS
				.getLocalFileSystem().fromLocalFile(f.getParentFile())));

		assertNull(EFSUtils.getRelativePath(EFS.getLocalFileSystem().fromLocalFile(f), EFS.getLocalFileSystem()
				.fromLocalFile(f2)));
	}
}
