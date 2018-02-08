/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * If tests fail, it may be because the default charset is non-UTF-8. You can simulate this in your env by setting a
 * command line JVM argument: -Dfile.encoding=US-ASCII
 * 
 * @author Shalom
 */
public class WriterOutputStreamTest
{
	private File testFile;

	@Before
	public void setUp() throws Exception
	{
		testFile = new File(FileUtil.getTempDirectory().toOSString(), "resources/test.txt");
		testFile.getParentFile().mkdirs();
	}

	@After
	public void tearDown() throws Exception
	{
		testFile.delete();
	}

	@Test
	public void testWriteUTF8() throws Exception
	{
		String toWrite = "Hello I'm a UTF-8 string that is using Umlauts - alt - älter - am ältesten";
		// The default FileWriter encoding is already a UTF-8, so no special writers decoration is needed.
		WriterOutputStream os = new WriterOutputStream(new OutputStreamWriter(new FileOutputStream(testFile),
				IOUtil.UTF_8), IOUtil.UTF_8);
		os.write(toWrite.getBytes(IOUtil.UTF_8));
		os.close();

		// Read back and test that the encoding is OK

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), IOUtil.UTF_8));
		String line = reader.readLine();
		reader.close();

		assertEquals("UFT-8 write-read process failed", toWrite, line);
	}

	@Test
	public void testWriteCP1255() throws Exception
	{
		String toWrite = "Hello I'm a CP-1255 string that has Hebrew - שלום";
		WriterOutputStream os = new WriterOutputStream(
				new OutputStreamWriter(new FileOutputStream(testFile), "CP1255"), "CP1255");
		os.write(toWrite.getBytes("CP1255"));
		os.flush();
		os.close();

		// Read back and test that the encoding is OK
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "CP1255"));
		String line = reader.readLine();
		reader.close();

		assertEquals("CP1255 write-read process failed", toWrite, line);
	}

	@Test
	public void testWriteBytes() throws Exception
	{
		String shortS = "P";
		String longS = "A B C";
		WriterOutputStream os = new WriterOutputStream(new FileWriter(testFile));
		byte[] bytes = longS.getBytes();

		os.write(shortS.getBytes()[0]);
		os.write(bytes, 0, 3);
		os.close();

		// Verify the written data
		BufferedReader reader = new BufferedReader(new FileReader(testFile));
		String line = reader.readLine();
		reader.close();

		assertEquals("Write-read process failed", "PA B", line);
	}

	@Test
	public void testWriteBytesWithCharset() throws Exception
	{
		String shortS = "P";
		String longS = "A B C";
		WriterOutputStream os = new WriterOutputStream(new FileWriter(testFile), IOUtil.UTF_8);
		byte[] bytes = longS.getBytes();

		os.write(shortS.getBytes()[0]);
		os.write(bytes, 0, 3);
		os.close();

		// Verify the written data
		BufferedReader reader = new BufferedReader(new FileReader(testFile));
		String line = reader.readLine();
		reader.close();

		assertEquals("Write-read process failed", "PA B", line);
	}
}
