/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

public class IOUtilTest
{
	private static final String BUNDLE_ID = "com.aptana.core.tests";
	private static final String RESOURCE_DIR = "resources";
	private static final String TEST_DIR = "copyTest";

	private static final String tempDir = FileUtil.getTempDirectory().toOSString();

	private Mockery context = new Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void testReadClosesStreamWhenIOExceptionThrown() throws Exception
	{
		final byte[] b = new byte[8192];
		final int off = 0;
		final int len = 8192;
		final InputStream stream = context.mock(InputStream.class);
		context.checking(new Expectations()
		{
			{
				oneOf(stream).markSupported();
				will(returnValue(false));
				oneOf(stream).read(b, off, len);
				will(throwException(new IOException("")));
				oneOf(stream).close();
			}
		});
		IOUtil.read(stream);
		context.assertIsSatisfied();
	}

	@Test
	public void testReadWithNoCharsetAndStreamDoesntSupportMarkAssumesUTF8() throws Exception
	{
		final byte[] b = new byte[8192];
		final int off = 0;
		final int len = 8192;
		final InputStream stream = context.mock(InputStream.class);
		context.checking(new Expectations()
		{
			{
				oneOf(stream).markSupported();
				will(returnValue(false));

				// Read a single byte/char to check for BOM
				oneOf(stream).read(b, off, len);
				will(returnValue(1));
				oneOf(stream).available();
				will(returnValue(-1));
				// Read content, but return empty
				oneOf(stream).read(b, off, len);
				will(returnValue(-1));
				// Close stream
				oneOf(stream).close();
			}
		});
		String result = IOUtil.read(stream);
		assertNotNull(result);
		// Returned one single 0 byte because we allowed reading of one...
		assertEquals(1, result.length());
		context.assertIsSatisfied();
	}

	// TODO Add tests for reading a known charset and for reading a BOM

	@Test
	public void testReadWithNoCharsetAndMarkSupportedSniffsCharset() throws Exception
	{
		final byte[] b = new byte[8192];
		final int off = 0;
		final int len = 8192;
		final InputStream stream = context.mock(InputStream.class);
		context.checking(new Expectations()
		{
			{
				// charset sniffing
				oneOf(stream).markSupported();
				will(returnValue(true));
				oneOf(stream).mark(8000);
				oneOf(stream).read(new byte[8000], 0, 8000);
				will(returnValue(-1));
				oneOf(stream).reset();
				oneOf(stream).reset();

				// Now do the reading
				oneOf(stream).read(b, off, len);
				// FIXME Read back actual bytes/chars/etc
				will(throwException(new IOException("")));
				oneOf(stream).close();
			}
		});
		IOUtil.read(stream);
		context.assertIsSatisfied();
	}

	@Test
	public void testReadWithNullInputReturnsNull() throws Exception
	{
		assertNull(IOUtil.read(null));
	}

	@Test
	public void testReadWithUnicodeCharacters() throws Exception
	{
		String stringwithUnicode = "abc\u5639\u563b";
		InputStream stream = new ByteArrayInputStream(stringwithUnicode.getBytes("UTF-8"));
		assertEquals(stringwithUnicode, IOUtil.read(stream, "UTF-8"));
	}

	@Test
	public void testReadWithMultipleLinesWithSlashN() throws Exception
	{
		String multilineString = "line one\nline two\nline three";
		InputStream stream = new ByteArrayInputStream(multilineString.getBytes("UTF-8"));
		assertEquals(multilineString, IOUtil.read(stream, "UTF-8"));
	}

	@Test
	public void testReadWithMultipleLinesWithSlashRSlashN() throws Exception
	{
		String multilineString = "line one\r\nline two\r\nline three";
		InputStream stream = new ByteArrayInputStream(multilineString.getBytes("UTF-8"));
		assertEquals("line one\r\nline two\r\nline three", IOUtil.read(stream, "UTF-8"));
	}

	@Test
	public void testReadWithMultipleLinesWithSlashR() throws Exception
	{
		String multilineString = "line one\rline two\rline three";
		InputStream stream = new ByteArrayInputStream(multilineString.getBytes("UTF-8"));
		assertEquals("line one\rline two\rline three", IOUtil.read(stream, "UTF-8"));
	}

	@Test
	public void testReadWithEndingnewline() throws Exception
	{
		String multilineString = "line one\r\nline two\r\nline three\r\n";
		InputStream stream = new ByteArrayInputStream(multilineString.getBytes("UTF-8"));
		assertEquals("line one\r\nline two\r\nline three\r\n", IOUtil.read(stream, "UTF-8"));
	}

	@Test
	public void testCopyDirectory() throws Exception
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		File resourceFolder = ResourceUtil.resourcePathToFile(resourceURL);

		File source = new File(resourceFolder, TEST_DIR);
		File dest = new File(tempDir, "tempdir");

		try
		{
			IOUtil.copyDirectory(source, dest);
			assertDirectory(source, dest);
		}
		finally
		{
			FileUtil.deleteRecursively(dest);
		}
	}

	@Test
	public void testCopyDirectorytoFile() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		File resourceFolder = ResourceUtil.resourcePathToFile(resourceURL);

		File source = new File(resourceFolder, TEST_DIR);
		File dest = new File(resourceFolder, "test.js");

		IOUtil.copyDirectory(source, dest);
		try
		{
			assertDirectory(source, dest);
			fail("Expected directories to not match");
		}
		catch (AssertionError ae)
		{
			// expected
		}
	}

	@Test
	public void testCopyFromNonReadableDirectory() throws Exception
	{
		// We can use source.setReadable(false) when we decide to use java 1.6
		if (!Platform.OS_WIN32.equals(Platform.getOS()))
		{
			URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
			File resourceFolder = ResourceUtil.resourcePathToFile(resourceURL);

			File source = new File(resourceFolder, TEST_DIR);
			File dest = new File(tempDir, "tempdir");

			try
			{
				Runtime.getRuntime().exec(new String[] { "chmod", "333", source.getAbsolutePath() }).waitFor(); //$NON-NLS-1$
				IOUtil.copyDirectory(source, dest);
				assertDirectory(source, dest);
				fail("Expected directories to not match");
			}
			catch (AssertionError ae)
			{
				// expected
			}
			finally
			{
				FileUtil.deleteRecursively(dest);
				Runtime.getRuntime().exec(new String[] { "chmod", "755", source.getAbsolutePath() }).waitFor(); //$NON-NLS-1$
			}
		}
	}

	@Test
	public void testCopyToNotWriteableExistingDirectory() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		File resourceFolder = ResourceUtil.resourcePathToFile(resourceURL);

		File source = new File(resourceFolder, TEST_DIR);
		File dest = new File(tempDir, "testdir");

		try
		{
			dest.mkdir();
			dest.setReadOnly();
			IOUtil.copyDirectory(source, dest);
			assertDirectory(source, dest);
			fail("Expected directories to not match");
		}
		catch (AssertionError ae)
		{
			// expected
		}
		finally
		{
			FileUtil.deleteRecursively(dest);
		}
	}

	@Test
	public void testCopyToNotWritableDirectory() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		File resourceFolder = ResourceUtil.resourcePathToFile(resourceURL);

		File source = new File(resourceFolder, TEST_DIR);
		File dest = new File(tempDir, "testdir");

		try
		{
			dest.mkdir();
			dest.setReadOnly();
			IOUtil.copyDirectory(source, new File(dest, "testdir2"));
			assertDirectory(source, dest);
			fail("Expected directories to not match");
		}
		catch (AssertionError ae)
		{
			// expected
		}
		finally
		{
			FileUtil.deleteRecursively(dest);
		}
	}

	@Test
	public void testExtractFile() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		File resourceFolder = ResourceUtil.resourcePathToFile(resourceURL);
		File dest = new File(tempDir, "testfile.txt");
		File sourceFile = new File(resourceFolder, "test.js");
		IPath sourcePath = new Path("resources/test.js");

		try
		{
			IOUtil.extractFile(BUNDLE_ID, sourcePath, dest);
			assertTrue(compareFiles(sourceFile, dest));
		}
		finally
		{
			dest.delete();
		}
	}

	@Test
	public void testExtractFileWithInvalidPath() throws IOException
	{
		File dest = new File(tempDir, "testfile");
		IPath source = new Path("invalid_file");

		IOUtil.extractFile(BUNDLE_ID, source, dest);
		assertFalse(dest.exists());
	}

	@Test
	public void testWrite() throws IOException
	{
		File dest = new File(tempDir, "test.txt");
		dest.createNewFile();
		String text = "This is a text for texting IOUtil.write()";
		OutputStream output = new FileOutputStream(dest);
		InputStream input = new FileInputStream(dest);
		try
		{

			IOUtil.write(output, text);
			assertTrue(IOUtil.read(input).equals(text));
		}
		finally
		{
			input.close();
			output.close();
			dest.delete();
		}
	}

	@Test
	public void testWriteWithNullText() throws IOException
	{
		File dest = new File(tempDir, "test.txt");
		dest.createNewFile();

		OutputStream output = new FileOutputStream(dest);
		InputStream input = new FileInputStream(dest);

		try
		{
			IOUtil.write(output, null);
			assertTrue(IOUtil.read(input).equals(StringUtil.EMPTY));
		}
		finally
		{
			input.close();
			output.close();
			dest.delete();
		}
	}

	@Test
	public void testPipe() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		File resourceFolder = ResourceUtil.resourcePathToFile(resourceURL);

		File source = new File(resourceFolder, "test.js");
		File dest = new File(tempDir, "test.txt");

		InputStream input = new FileInputStream(source);
		OutputStream output = new FileOutputStream(dest);

		try
		{
			IOUtil.pipe(input, output);

			assertTrue(compareFiles(source, dest));
		}
		finally
		{
			input.close();
			output.close();
			dest.delete();
		}
	}

	private void assertDirectory(File directory1, File directory2)
	{
		assertTrue(MessageFormat.format("{0} is not a directory", directory1.getAbsolutePath()),
				directory1.isDirectory());
		assertTrue(MessageFormat.format("{0} is not a directory", directory2.getAbsolutePath()),
				directory2.isDirectory());

		List<File> fileList1 = Arrays.asList(directory1.listFiles());
		List<File> fileList2 = Arrays.asList(directory2.listFiles());
		assertEquals(MessageFormat.format("Expected same # of children in dirs {0} and {1}",
				directory1.getAbsolutePath(), directory2.getAbsolutePath()), fileList1.size(), fileList2.size());

		List<String> fileNames = Arrays.asList(directory2.list());
		for (File file : fileList1)
		{
			assertTrue("{0} doesn't contain", fileNames.contains(file.getName()));

			File file2 = fileList2.get(fileNames.indexOf(file.getName()));

			if (file.isDirectory())
			{
				assertDirectory(file, file2);
			}
			else
			{
				try
				{
					if (!compareFiles(file, file2))
					{
						fail(MessageFormat.format("Files {0} and {1} don't match", file.getAbsolutePath(),
								file2.getAbsolutePath()));
					}
				}
				catch (FileNotFoundException e)
				{
					fail(e.getMessage());
				}
			}
		}
	}

	private boolean compareFiles(File file1, File file2) throws FileNotFoundException
	{
		return file1.length() == file2.length();
	}
}
