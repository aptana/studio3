/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

@SuppressWarnings("nls")
public class ZipUtilTest extends TestCase
{

	private static final String BUNDLE_ID = "com.aptana.core.tests";

	private static final String STREAM_TEST_ZIP = "resources/streamtest.zip";

	private static final String TEST_ZIP = "resources/test.zip";
	private static final String TEST_OVERWRITE_ZIP = "resources/test_overwrite.zip";
	private static final String TEST_OVERWRITE_DIR_ZIP = "resources/test_overwrite_dir.zip";
	private static final String TEST_ZIP_SYMLINKS = "resources/test_symlinks.zip";
	private static final HashSet<String> TOP_ENTRIES = new HashSet<String>(Arrays.asList("folder", "file.txt"));
	private static final HashSet<String> TOP_OVERWRITE_ENTRIES = new HashSet<String>(Arrays.asList("folder",
			"newFolder", "file.txt"));
	private static final HashSet<String> TOP_ENTRIES_SYMLINK = new HashSet<String>(Arrays.asList("folder", "file.txt",
			"filesym.txt", "othersym"));

	public void testUnzipFile() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(TEST_ZIP);
		assertNotNull(resourceURL);
		File resourceFile = ResourceUtil.resourcePathToFile(resourceURL);
		assertNotNull(resourceFile);

		File destinationDir = File.createTempFile(getClass().getSimpleName(), null);
		assertTrue(destinationDir.delete());
		assertTrue(destinationDir.mkdirs());

		try
		{
			assertTrue(ZipUtil.extract(resourceFile, destinationDir, new NullProgressMonitor()).isOK());

			File[] files = destinationDir.listFiles();
			assertEquals("Unzipped contents to not match expected number of files", TOP_ENTRIES.size(), files.length);

			for (File file : files)
			{
				assertTrue("Unexpected zip entry " + file.getName(), TOP_ENTRIES.contains(file.getName()));
			}
			assertTrue("Expected entry is not a directory", new File(destinationDir, "folder").isDirectory());
			assertTrue("Expected entry is not a file", new File(destinationDir, "file.txt").isFile());
			assertTrue("Expected entry is not a directory", new File(destinationDir, "folder/other").isDirectory());
			assertTrue("Expected entry is not a file", new File(destinationDir, "folder/file.txt").isFile());
		}
		finally
		{
			// remove the contents after we are done with the test
			FileUtil.deleteRecursively(destinationDir);
		}
	}

	/**
	 * Test unzipping the test.zip with an overwrite flag (expected to act the same as without the overwrite)
	 * 
	 * @throws Exception
	 */
	public void testUnzipFileWithOverwrite() throws Exception
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(TEST_ZIP);
		assertNotNull(resourceURL);
		File resourceFile = ResourceUtil.resourcePathToFile(resourceURL);
		assertNotNull(resourceFile);

		File destinationDir = File.createTempFile(getClass().getSimpleName(), null);
		assertTrue(destinationDir.delete());
		assertTrue(destinationDir.mkdirs());

		try
		{
			// Extract once.
			assertTrue(ZipUtil.extract(resourceFile, destinationDir, new NullProgressMonitor()).isOK());
			// Extract again, with an overwrite mode.
			assertTrue(ZipUtil.extract(resourceFile, destinationDir, true, new NullProgressMonitor()).isOK());

			File[] files = destinationDir.listFiles();
			assertEquals("Unzipped contents to not match expected number of files", TOP_ENTRIES.size(), files.length);

			for (File file : files)
			{
				assertTrue("Unexpected zip entry " + file.getName(), TOP_ENTRIES.contains(file.getName()));
			}
			assertTrue("Expected entry is not a directory", new File(destinationDir, "folder").isDirectory());
			assertTrue("Expected entry is not a file", new File(destinationDir, "file.txt").isFile());
			assertTrue("Expected entry is not a directory", new File(destinationDir, "folder/other").isDirectory());
			assertTrue("Expected entry is not a file", new File(destinationDir, "folder/file.txt").isFile());
		}
		finally
		{
			// remove the contents after we are done with the test
			FileUtil.deleteRecursively(destinationDir);
		}
	}

	/**
	 * Test unzipping the test_overwrite with an overwrite flag.
	 * 
	 * @throws Exception
	 */
	public void testUnzipWithChangedOverwrite() throws Exception
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(TEST_ZIP);
		URL overwriteResourceURL = Platform.getBundle(BUNDLE_ID).getEntry(TEST_OVERWRITE_ZIP);
		assertNotNull(resourceURL);
		assertNotNull(overwriteResourceURL);

		File resourceFile = ResourceUtil.resourcePathToFile(resourceURL);
		File overwriteResourceFile = ResourceUtil.resourcePathToFile(overwriteResourceURL);

		assertNotNull(resourceFile);
		assertNotNull(overwriteResourceFile);

		File destinationDir = File.createTempFile(getClass().getSimpleName(), null);
		assertTrue(destinationDir.delete());
		assertTrue(destinationDir.mkdirs());

		try
		{
			// Extract once.
			assertTrue(ZipUtil.extract(resourceFile, destinationDir, new NullProgressMonitor()).isOK());
			// Extract again, with an overwrite mode.
			assertTrue(ZipUtil.extract(overwriteResourceFile, destinationDir, true, new NullProgressMonitor()).isOK());

			File[] files = destinationDir.listFiles();
			assertEquals("Unzipped contents to not match expected number of files", TOP_OVERWRITE_ENTRIES.size(),
					files.length);

			for (File file : files)
			{
				assertTrue("Unexpected zip entry " + file.getName(), TOP_OVERWRITE_ENTRIES.contains(file.getName()));
			}
			assertTrue("Expected entry is not a directory", new File(destinationDir, "folder").isDirectory());
			assertTrue("Expected entry is not a directory", new File(destinationDir, "newFolder").isDirectory());
			assertTrue("Expected entry is not a file", new File(destinationDir, "file.txt").isFile());
			assertTrue("Expected entry is not a directory", new File(destinationDir, "folder/other").isDirectory());
			assertTrue("Expected entry is not a file", new File(destinationDir, "folder/other/hello.txt").isFile());
			assertTrue("Expected entry is not a file", new File(destinationDir, "folder/file.txt").isFile());
			assertTrue("Expected entry is not a file", new File(destinationDir, "newFolder/newFile.txt").isFile());

			// The file.txt should now contain the string "new content"
			assertEquals("Expected 'new content' as the overitten content of 'file.txt'", "new content",
					IOUtil.read(new FileInputStream(new File(destinationDir, "folder/file.txt"))));
		}
		finally
		{
			// remove the contents after we are done with the test
			FileUtil.deleteRecursively(destinationDir);
		}
	}

	/*
	 * Test a case where the overriding Zip has a file that overwrite a folder on the target directory.
	 */
	public void testOverwriteDirWithFile() throws Exception
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(TEST_ZIP);
		URL overwriteResourceURL = Platform.getBundle(BUNDLE_ID).getEntry(TEST_OVERWRITE_DIR_ZIP);
		assertNotNull(resourceURL);
		assertNotNull(overwriteResourceURL);

		File resourceFile = ResourceUtil.resourcePathToFile(resourceURL);
		File overwriteResourceFile = ResourceUtil.resourcePathToFile(overwriteResourceURL);

		assertNotNull(resourceFile);
		assertNotNull(overwriteResourceFile);

		File destinationDir = File.createTempFile(getClass().getSimpleName(), null);
		assertTrue(destinationDir.delete());
		assertTrue(destinationDir.mkdirs());

		try
		{
			// Extract once.
			assertTrue(ZipUtil.extract(resourceFile, destinationDir, new NullProgressMonitor()).isOK());
			// Extract again, with an overwrite mode.
			// assertTrue(ZipUtil.extract(overwriteResourceFile, destinationDir, true, new NullProgressMonitor()).isOK());

			File[] files = destinationDir.listFiles();
			assertEquals("Unzipped contents to not match expected number of files", 2, files.length);

			// The previous 'folder' directory should now be a file.
			assertFalse("Expected entry is not a file", new File(destinationDir, "folder").isFile());
			assertTrue("Expected entry is not a file", new File(destinationDir, "file.txt").isFile());
		}
		finally
		{
			// remove the contents after we are done with the test
			FileUtil.deleteRecursively(destinationDir);
		}
	}

	public void testUnzipFileWithSymlinks() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(TEST_ZIP_SYMLINKS);
		assertNotNull(resourceURL);
		File resourceFile = ResourceUtil.resourcePathToFile(resourceURL);
		assertNotNull(resourceFile);

		File destinationDir = File.createTempFile(getClass().getSimpleName(), null);
		assertTrue(destinationDir.delete());
		assertTrue(destinationDir.mkdirs());

		try
		{
			assertTrue(ZipUtil.extract(resourceFile, destinationDir, new NullProgressMonitor()).isOK());

			File[] files = destinationDir.listFiles();
			assertEquals("Unzipped contents to not match expected number of files", TOP_ENTRIES_SYMLINK.size(),
					files.length);

			for (File file : files)
			{
				assertTrue("Unexpected zip entry " + file.getName(), TOP_ENTRIES_SYMLINK.contains(file.getName()));
			}
			assertTrue("Expected entry is not a directory", new File(destinationDir, "folder").isDirectory());
			assertTrue("Expected entry is not a file", new File(destinationDir, "file.txt").isFile());
			assertTrue("Expected entry is not a directory", new File(destinationDir, "folder/other").isDirectory());
			assertTrue("Expected entry is not a file", new File(destinationDir, "folder/file.txt").isFile());

			assertTrue("Expected entry is not a symlink", FileUtil.isSymlink(new File(destinationDir, "filesym.txt")));
			assertTrue("Expected entry is not a symlink", FileUtil.isSymlink(new File(destinationDir, "othersym")));
		}
		finally
		{
			// remove the contents after we are done with the test
			FileUtil.deleteRecursively(destinationDir);
		}
	}

	public void testZipFile() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(TEST_ZIP);
		assertNotNull(resourceURL);
		File resourceFile = ResourceUtil.resourcePathToFile(resourceURL);
		assertNotNull(resourceFile);

		File destinationDir = File.createTempFile(getClass().getSimpleName(), null);
		assertTrue(destinationDir.delete());
		assertTrue(destinationDir.mkdirs());

		File destinationDir2 = File.createTempFile(getClass().getSimpleName(), null);
		assertTrue(destinationDir2.delete());
		assertTrue(destinationDir2.mkdirs());

		try
		{
			assertTrue(ZipUtil.extract(resourceFile, destinationDir, new NullProgressMonitor()).isOK());

			File[] files = destinationDir.listFiles();
			assertEquals("Unzipped contents to not match expected number of files", TOP_ENTRIES.size(), files.length);

			String[] paths = new String[files.length];
			int i = 0;
			for (File file : files)
			{
				String name = file.getName();
				assertTrue("Unexpected zip entry " + file.getName(), TOP_ENTRIES.contains(name));
				paths[i] = new Path(destinationDir.getAbsolutePath()).append(name).toOSString();
				i++;
			}

			assertTrue("Expected entry is not a directory", new File(destinationDir, "folder").isDirectory());
			assertTrue("Expected entry is not a file", new File(destinationDir, "file.txt").isFile());
			assertTrue("Expected entry is not a directory", new File(destinationDir, "folder/other").isDirectory());
			assertTrue("Expected entry is not a file", new File(destinationDir, "folder/file.txt").isFile());

			IPath zipFilePath = Path.fromOSString(destinationDir2.getAbsolutePath())
					.append(String.valueOf(System.currentTimeMillis())).addFileExtension("zip");
			assertTrue("Compression failed", ZipUtil.compress(zipFilePath.toOSString(), paths));

			IPath zipFilePath2 = Path.fromOSString(destinationDir2.getAbsolutePath())
					.append(String.valueOf(System.currentTimeMillis())).addFileExtension("zip");
			assertTrue(
					"Compression to relative path failed",
					ZipUtil.compress(zipFilePath2.toOSString(), paths, Path.fromOSString(paths[0])
							.removeLastSegments(1).toOSString()));

			assertTrue(ZipUtil.extract(resourceFile, destinationDir2, new NullProgressMonitor()).isOK());

			zipFilePath.toFile().delete();
			zipFilePath2.toFile().delete();

			files = destinationDir2.listFiles();
			assertEquals("Unzipped contents to not match expected number of files", TOP_ENTRIES.size(), files.length);

			for (File file : files)
			{
				String name = file.getName();
				assertTrue("Unexpected zip entry " + file.getName(), TOP_ENTRIES.contains(name));
			}

			assertTrue("Expected entry is not a directory", new File(destinationDir2, "folder").isDirectory());
			assertTrue("Expected entry is not a file", new File(destinationDir2, "file.txt").isFile());
			assertTrue("Expected entry is not a directory", new File(destinationDir2, "folder/other").isDirectory());
			assertTrue("Expected entry is not a file", new File(destinationDir2, "folder/file.txt").isFile());

		}
		finally
		{
			// remove the contents after we are done with the test
			FileUtil.deleteRecursively(destinationDir);
			FileUtil.deleteRecursively(destinationDir2);
		}
	}

	public void testOpenEntry() throws IOException
	{
		URL zipURL = Platform.getBundle(BUNDLE_ID).getEntry(STREAM_TEST_ZIP);
		File zipFile = ResourceUtil.resourcePathToFile(zipURL);
		InputStream stream;

		// Open entry should return null when it can't find the specified file
		assertNull(ZipUtil.openEntry(zipFile, Path.fromOSString("test.haml")));

		stream = ZipUtil.openEntry(zipFile, Path.fromOSString("streamtest"));

		String text = IOUtil.read(stream);
		assertEquals("this is a test\n\n", text);
	}

}
