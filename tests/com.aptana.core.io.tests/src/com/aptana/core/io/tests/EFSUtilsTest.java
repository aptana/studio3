package com.aptana.core.io.tests;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.internal.filesystem.local.LocalFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.ide.core.io.efs.EFSUtils;

public class EFSUtilsTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetFileStoreIResource() {
		//fail("Not yet implemented");
	}

	public void testGetFileStoreFile() {
		//fail("Not yet implemented");
	}

	public void testSetModificationTime() {
		//fail("Not yet implemented");
	}

	public void testGetFilesIFileStore() {
		//fail("Not yet implemented");
	}

	public void testGetFilesIFileStoreBooleanBoolean() {
		//fail("Not yet implemented");
	}

	public void testGetParentFile() {
		//fail("Not yet implemented");
	}

	public void testGetAbsolutePath() throws IOException, CoreException {
		File f = File.createTempFile("test", "txt");
		LocalConnectionPoint lcp = new LocalConnectionPoint(Path.fromOSString(f.getAbsolutePath()));
		assertEquals(f.getAbsolutePath(), EFSUtils.getAbsolutePath(lcp.getRoot()));
	}

	public void testGetPath() throws IOException, CoreException {
		File f = File.createTempFile("test", "txt");
		LocalConnectionPoint lcp = new LocalConnectionPoint(Path.fromOSString(f.getAbsolutePath()));
		assertEquals(f.getAbsolutePath(), EFSUtils.getAbsolutePath(lcp.getRoot()));
	}

	public void testGetRelativePath() throws IOException, CoreException {
		File f = File.createTempFile("test", "txt");
		assertEquals("/" + f.getName(), EFSUtils.getRelativePath(new LocalFile(f.getParentFile()), new LocalFile(f)));
	}
}
