/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
		String path = "c:/Documents and Settings/username/My Documents/workspace/whatever.txt";
		assertEquals("c:/.../username/My Documents/workspace/whatever.txt", FileUtil.compressPath(path, 50));
	}

	public void testCompressLeadingPath()
	{
		String path = "c:/Documents and Settings/username/My Documents/workspace/whatever.txt";
		assertEquals(".../username/My Documents/workspace/whatever.txt", FileUtil.compressLeadingPath(path, 50));
	}

	public void testGetExtension()
	{
		String filename = "test.html";
		assertEquals("html", FileUtil.getExtension(filename));
	}

	public void testEmptyExtension()
	{
		String filename = "test";
		assertEquals(StringUtil.EMPTY, FileUtil.getExtension(filename));
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

	public void testWriteStringToFile() throws IOException
	{

		File temp = File.createTempFile("test", "txt");
		temp.deleteOnExit();

		String testText = "This is a test" + FileUtil.NEW_LINE + "And a new line";
		FileUtil.writeStringToFile(testText, temp);

		StringBuffer contents = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(temp));
		String text = "";
		while ((text = reader.readLine()) != null)
		{
			if (contents.length() > 0)
			{
				contents.append(FileUtil.NEW_LINE);
			}
			contents.append(text);
		}

		assertEquals(testText, contents.toString());
	}
}
