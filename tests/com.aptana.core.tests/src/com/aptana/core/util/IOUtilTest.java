/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

public class IOUtilTest extends TestCase
{
	private static final String BUNDLE_ID = "com.aptana.core.tests";
	private static final String RESOURCE_DIR = "resources";

	private Mockery context = new Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	public void testRead() throws Exception
	{
		final byte[] b = new byte[8192];
		final int off = 0;
		final int len = 8192;
		final InputStream stream = context.mock(InputStream.class);
		context.checking(new Expectations()
		{
			{
				oneOf(stream).read(b, off, len);
				will(throwException(new IOException("")));
				oneOf(stream).close();
			}
		});
		IOUtil.read(stream);
		context.assertIsSatisfied();
	}

	public void testReadWithNullInputReturnsNull() throws Exception
	{
		assertNull(IOUtil.read(null));
	}

	public void testReadWithUnicodeCharacters() throws Exception
	{
		String stringwithUnicode = "abc\u5639\u563b";
		InputStream stream = new ByteArrayInputStream(stringwithUnicode.getBytes("UTF-8"));
		assertEquals(stringwithUnicode, IOUtil.read(stream, "UTF-8"));
	}

	public void testReadWithMultipleLinesWithSlashN() throws Exception
	{
		String multilineString = "line one\nline two\nline three";
		InputStream stream = new ByteArrayInputStream(multilineString.getBytes("UTF-8"));
		assertEquals(multilineString, IOUtil.read(stream, "UTF-8"));
	}

	public void testReadWithMultipleLinesWithSlashRSlashN() throws Exception
	{
		String multilineString = "line one\r\nline two\r\nline three";
		InputStream stream = new ByteArrayInputStream(multilineString.getBytes("UTF-8"));
		assertEquals("line one\r\nline two\r\nline three", IOUtil.read(stream, "UTF-8"));
	}

	public void testReadWithMultipleLinesWithSlashR() throws Exception
	{
		String multilineString = "line one\rline two\rline three";
		InputStream stream = new ByteArrayInputStream(multilineString.getBytes("UTF-8"));
		assertEquals("line one\rline two\rline three", IOUtil.read(stream, "UTF-8"));
	}

	public void testReadWithEndingnewline() throws Exception
	{
		String multilineString = "line one\r\nline two\r\nline three\r\n";
		InputStream stream = new ByteArrayInputStream(multilineString.getBytes("UTF-8"));
		assertEquals("line one\r\nline two\r\nline three\r\n", IOUtil.read(stream, "UTF-8"));
	}

	public void testCopyDirectory() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		File resourceFolder = ResourceUtil.resourcePathToFile(resourceURL);

		File source = new File(resourceFolder, "copyTest");
		File dest = new File(resourceFolder, "tempdir");

		IOUtil.copyDirectory(source, dest);

		assertTrue(compareDirectory(source, dest));

		FileUtil.deleteRecursively(dest);
	}

	private boolean compareDirectory(File directory1, File directory2)
	{
		if (!directory1.isDirectory() || !directory2.isDirectory())
		{
			return false;
		}

		List<String> fileNames = Arrays.asList(directory2.list());

		List<File> fileList1 = Arrays.asList(directory1.listFiles());
		List<File> fileList2 = Arrays.asList(directory2.listFiles());

		if (fileList1.size() != fileList2.size())
		{
			return false;
		}

		for (File file : fileList1)
		{
			if (!fileNames.contains(file.getName()))
			{
				return false;
			}

			File file2 = fileList2.get(fileNames.indexOf(file.getName()));

			if (file.isDirectory())
			{
				if (!compareDirectory(file, file2))
				{
					return false;
				}
			}
			else
			{
				try
				{
					if (!compareFiles(file, file2))
					{
						return false;
					}
				}
				catch (FileNotFoundException e)
				{
					return false;
				}
			}
		}
		return true;
	}

	private boolean compareFiles(File file1, File file2) throws FileNotFoundException
	{
		return IOUtil.read(new FileInputStream(file1)).equals(IOUtil.read(new FileInputStream(file2)));
	}
}
