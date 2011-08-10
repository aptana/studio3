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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

public class IOUtilTest extends TestCase
{
	private static final String BUNDLE_ID = "com.aptana.core.tests";
	private static final String RESOURCE_DIR = "resources";
	private static final String TEST_DIR = "copyTest";

	private static final String tempDir = System.getProperty("java.io.tmpdir");

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

		File source = new File(resourceFolder, TEST_DIR);
		File dest = new File(tempDir, "tempdir");

		IOUtil.copyDirectory(source, dest);
		assertTrue(compareDirectory(source, dest));
		FileUtil.deleteRecursively(dest);
	}

	public void testCopyDirectorytoFile() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		File resourceFolder = ResourceUtil.resourcePathToFile(resourceURL);

		File source = new File(resourceFolder, TEST_DIR);
		File dest = new File(resourceFolder, "test.js");

		IOUtil.copyDirectory(source, dest);
		assertFalse(compareDirectory(source, dest));
	}

	public void testCopyFromReadOnlyDirectory() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		File resourceFolder = ResourceUtil.resourcePathToFile(resourceURL);

		File source = new File(resourceFolder, TEST_DIR);
		File dest = new File(tempDir, "tempdir");

		source.setReadable(false);
		IOUtil.copyDirectory(source, dest);
		assertFalse(compareDirectory(source, dest));
		FileUtil.deleteRecursively(dest);
		source.setReadable(true);
	}

	public void testCopyToNotWriteableExistingDirectory() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		File resourceFolder = ResourceUtil.resourcePathToFile(resourceURL);

		File source = new File(resourceFolder, TEST_DIR);
		File dest = new File(tempDir, "testdir");

		dest.mkdir();
		dest.setReadOnly();
		IOUtil.copyDirectory(source, dest);
		assertFalse(compareDirectory(source, dest));
		FileUtil.deleteRecursively(dest);
	}

	public void testCopyToNotWritableDirectory() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		File resourceFolder = ResourceUtil.resourcePathToFile(resourceURL);

		File source = new File(resourceFolder, TEST_DIR);
		File dest = new File(tempDir, "testdir");

		dest.mkdir();
		dest.setReadOnly();
		IOUtil.copyDirectory(source, new File(dest, "testdir2"));
		assertFalse(compareDirectory(source, dest));
		FileUtil.deleteRecursively(dest);
	}

	public void testExtractFile() throws IOException
	{
		File dest = new File(tempDir, "testfile.txt");
		IPath source = new Path("resources/test.js");

		IOUtil.extractFile(BUNDLE_ID, source, dest);
		assertTrue(compareFiles(source.toFile(), dest));
		dest.delete();
	}

	public void testExtractFileWithInvalidPath() throws IOException
	{
		IPath source = new Path("invalid_file");
		File dest = new File(tempDir, "testfile.txt");

		IOUtil.extractFile(BUNDLE_ID, source, dest);

		assertFalse(dest.exists());

	}

	public void testWrite() throws IOException
	{
		File dest = new File(tempDir, "test.txt");
		dest.createNewFile();
		String text = "This is a text for texting IOUtil.write()";

		OutputStream output = new FileOutputStream(dest);
		InputStream input = new FileInputStream(dest);

		IOUtil.write(output, text);
		assertTrue(IOUtil.read(input).equals(text));

		input.close();
		output.close();
		dest.delete();
	}

	public void testWriteWithNullText() throws IOException
	{
		File dest = new File(tempDir, "test.txt");
		dest.createNewFile();

		OutputStream output = new FileOutputStream(dest);
		InputStream input = new FileInputStream(dest);

		IOUtil.write(output, null);
		assertTrue(IOUtil.read(input).equals(StringUtil.EMPTY));

		input.close();
		output.close();
		dest.delete();
	}

	public void testPipe() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		File resourceFolder = ResourceUtil.resourcePathToFile(resourceURL);

		File source = new File(resourceFolder, "test.js");
		File dest = new File(tempDir, "test.txt");

		InputStream input = new FileInputStream(source);
		OutputStream output = new FileOutputStream(dest);

		IOUtil.pipe(input, output);

		assertTrue(compareFiles(source, dest));

		input.close();
		output.close();
		dest.delete();

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
		return file1.length() == file2.length();
	}
}
