package com.aptana.core.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

public class FileUtilTest extends TestCase
{
	public void testIsDirectoryAccessible()
	{
	}

	public void testCompressPath()
	{
	}

	public void testCompressLeadingPath()
	{
	}

	public void testGetExtension()
	{
	}

	public void testGetRandomFileName()
	{
	}

	public void testDeleteRecursively()
	{
	}

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
}
