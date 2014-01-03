/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

public class FileUtilTest
{

	@Test
	public void testIsDirectoryAccessible()
	{
		assertFalse("null directory argument should return false", FileUtil.isDirectoryAccessible(null));
		IPath tmp = FileUtil.getTempDirectory();
		IPath subdir = tmp.append("subdir" + System.currentTimeMillis());
		assertTrue("tmp dir should be accessible", FileUtil.isDirectoryAccessible(tmp.toFile()));
		assertFalse("Non-existant directory shouldn't be accessible", FileUtil.isDirectoryAccessible(subdir.toFile()));
		assertTrue("Failed to create subdir of tmp dir", subdir.toFile().mkdirs());
		assertTrue("After creating subdir, it should be accessible", FileUtil.isDirectoryAccessible(subdir.toFile()));
		// TODO Use chmod to not allow directory to be accessible?
	}

	@Test
	public void testCompressPath()
	{
		String path = "c:/Documents and Settings/username/My Documents/workspace/whatever.txt";
		assertEquals("c:/.../username/My Documents/workspace/whatever.txt", FileUtil.compressPath(path, 50));
		assertEquals(path, FileUtil.compressPath(path, 100));
	}

	@Test
	public void testCompressPathForNull()
	{
		assertNull(FileUtil.compressPath(null, 10));
	}

	@Test
	public void testCompressPathNoSlash()
	{
		String path = "a_path_with_no_slash";
		assertEquals(path, FileUtil.compressPath(path, 10));
	}

	@Test
	public void testCompressPathSingleSlash()
	{
		String path = "test/compress_path";
		assertEquals(path, FileUtil.compressPath(path, 10));
		assertEquals(path, FileUtil.compressPath(path, 15));
	}

	@Test
	public void testCompressPathLastPathLongerThanDesiredLength()
	{
		String path = "test/compress/a_really_long_last_path";
		assertEquals("test/.../a_really_long_last_path", FileUtil.compressPath(path, 30));
	}

	@Test
	public void testCompressLeadingPath()
	{
		String path = "c:/Documents and Settings/username/My Documents/workspace/whatever.txt";
		assertEquals(".../username/My Documents/workspace/whatever.txt", FileUtil.compressLeadingPath(path, 50));
		assertEquals(path, FileUtil.compressLeadingPath(path, 100));
	}

	@Test
	public void testCompressLeadingPathForNull()
	{
		assertNull(FileUtil.compressLeadingPath(null, 10));
	}

	@Test
	public void testCompressLeadingPathLastPathLongerThanDesiredLength()
	{
		String path = "test/a_really_long_last_path";
		assertEquals(path, FileUtil.compressLeadingPath(path, 20));
	}

	@Test
	public void testGetExtension()
	{
		String filename = "test.html";
		assertEquals("html", FileUtil.getExtension(filename));
	}

	@Test
	public void testEmptyExtension()
	{
		String filename = "test";
		assertEquals(StringUtil.EMPTY, FileUtil.getExtension(filename));
	}

	@Test
	public void testGetExtensionForNull()
	{
		assertNull(FileUtil.getExtension(null));
	}

	@Test
	public void testGetRandomFileName()
	{
		String prefix = "test";
		String suffix = ".html";

		String filename = FileUtil.getRandomFileName(prefix, suffix);
		assertTrue(filename.startsWith(prefix));
		assertTrue(filename.endsWith(suffix));

		int beginIndex = filename.indexOf(prefix) + prefix.length();
		int endIndex = filename.lastIndexOf(suffix);
		try
		{
			Long.parseLong(filename.substring(beginIndex, endIndex));
		}
		catch (NumberFormatException e)
		{
			fail();
		}
	}

	@Test
	public void testGetRandomFileNameWithNullPrefixSuffix()
	{
		String filename = FileUtil.getRandomFileName(null, null);
		try
		{
			Long.parseLong(filename);
		}
		catch (NumberFormatException e)
		{
			fail();
		}
	}

	@Test
	public void testDeleteRecursively() throws Exception
	{
		File rootDir = File.createTempFile("deleteTest", Long.toString(System.nanoTime()));
		rootDir.delete();
		rootDir.mkdir();

		File level2Dir = new File(rootDir, "folder");
		level2Dir.mkdir();

		int count = 5;
		File[] files = new File[count];
		for (int i = 0; i < count; ++i)
		{
			files[i] = File.createTempFile("file", Long.toString(System.nanoTime()), level2Dir);
		}

		FileUtil.deleteRecursively(rootDir);

		assertFalse(rootDir.exists());
		assertFalse(level2Dir.exists());
		for (File file : files)
		{
			assertFalse(file.exists());
		}
	}

	@Test
	public void testDeleteRecursivelyForNull()
	{
		assertFalse(FileUtil.deleteRecursively(null));
	}

	@Test
	public void testGatherFilesFromCommandLineArguments()
	{
		String[] arguments = { "-os", "macosx", "-ws", "cocoa", "-arch", "x86", "-debug", "-keyring",
				"/Users/ingo/.eclipse_keyring", "-consoleLog", "-showlocation" };
		List<File> files = FileUtil.gatherFilesFromCommandLineArguments(arguments);
		assertEquals(0, files.size());

		try
		{
			File f = File.createTempFile("testGatherFilesFromCommandLineArguments", ".txt");
			f.createNewFile();
			f.deleteOnExit();

			arguments = new String[] { "-os", "win32", "-ws", "win32", "-arch", "x86", "-debug", f.getAbsolutePath() };
			files = FileUtil.gatherFilesFromCommandLineArguments(arguments);

			assertEquals(1, files.size());
			assertEquals(f.getAbsolutePath(), files.get(0).getAbsolutePath());
		}
		catch (IOException e)
		{
			fail("Unable to test parsing of file name from command line");
		}
	}

	@Test
	public void testCountFilesWithNullArg()
	{
		assertEquals(0, FileUtil.countFiles(null));
	}

	@Test
	public void testCountFilesWithSingleFile() throws Exception
	{
		File file = File.createTempFile("delete_me", null);
		file.deleteOnExit();
		assertEquals(1, FileUtil.countFiles(file));
	}

	@Test
	public void testCountFilesWithDirectory() throws Exception
	{
		File dir = FileUtil.getTempDirectory().append("count_dir_" + System.currentTimeMillis()).toFile();
		try
		{
			dir.mkdirs();
			int fileCount = 10;
			for (int i = 0; i < fileCount; i++)
			{
				new File(dir, Integer.toString(i)).createNewFile();
			}
			assertEquals(fileCount, FileUtil.countFiles(dir));
		}
		finally
		{
			FileUtil.deleteRecursively(dir);
		}
	}

	@Test
	public void testCountFilesWithMultipleDirectories() throws Exception
	{
		File dir = FileUtil.getTempDirectory().append("count_dir_" + System.currentTimeMillis()).toFile();
		try
		{
			dir.mkdirs();
			int dirCount = 5;
			int fileCount = 10;
			for (int j = 0; j < dirCount; j++)
			{
				File subDir = new File(dir, "dir_" + Integer.toString(j));
				subDir.mkdirs();
				for (int i = 0; i < fileCount; i++)
				{
					new File(subDir, Integer.toString(i)).createNewFile();
				}
			}

			assertEquals(dirCount * fileCount, FileUtil.countFiles(dir));
		}
		finally
		{
			FileUtil.deleteRecursively(dir);
		}
	}

	// TODO Add test for countFiles with symlink loop?
	@Test
	public void testCountFilesWithMultipleDirectoriesAndSymlinkLoop() throws Exception
	{
		File dir = FileUtil.getTempDirectory().append("count_dir_" + System.currentTimeMillis()).toFile();
		try
		{
			dir.mkdirs();
			int dirCount = 5;
			int fileCount = 10;
			for (int j = 0; j < dirCount; j++)
			{
				File subDir = new File(dir, "dir_" + Integer.toString(j));
				subDir.mkdirs();
				for (int i = 0; i < fileCount; i++)
				{
					new File(subDir, Integer.toString(i)).createNewFile();
				}

				IStatus status = ProcessUtil.runInBackground("ln", Path.fromOSString(subDir.getAbsolutePath()), "-s",
						dir.getAbsolutePath(), "symlink");
				assertTrue(status.isOK());
			}
			assertEquals(dirCount * (fileCount + 1), FileUtil.countFiles(dir));
		}
		finally
		{
			FileUtil.deleteRecursively(dir);
		}
	}

	@Test
	public void testChmodAndGetPermissions() throws Exception
	{
		if (PlatformUtil.isWindows())
		{
			return;
		}

		File file = File.createTempFile("chmod", null);
		IPath filePath = Path.fromOSString(file.getAbsolutePath());
		Random r = new Random();
		try
		{
			// Spot check 10 permissions
			for (int n = 0; n < 10; n++)
			{
				int i = r.nextInt(512);
				String permString = StringUtil.pad(Integer.toString(i, 8), 3, '0');
				FileUtil.chmod(permString, file);
				assertEquals(permString, FileUtil.getPermissions(filePath));
			}
		}
		finally
		{
			file.delete();
		}
	}
}
