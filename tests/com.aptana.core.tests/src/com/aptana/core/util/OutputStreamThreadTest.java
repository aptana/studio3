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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import junit.framework.TestCase;

public class OutputStreamThreadTest
{
	@Test
	public void testNoCharSet() throws FileNotFoundException, IOException, InterruptedException
	{
		File outputFile = FileUtil.createTempFile("test", "txt");
		OutputStream output = new FileOutputStream(outputFile);

		OutputStreamThread outputThread = new OutputStreamThread(output, "this is a test", null);

		outputThread.start();
		outputThread.join();

		String result = IOUtil.read(new FileInputStream(outputFile));
		assertEquals("this is a test", result);
	}

	@Test
	public void testWithUTF8CharSet() throws FileNotFoundException, IOException, InterruptedException
	{
		File outputFile = FileUtil.createTempFile("test", "txt");
		OutputStream output = new FileOutputStream(outputFile);

		OutputStreamThread outputThread = new OutputStreamThread(output, "this is a test �", "UTF-8");

		outputThread.start();
		outputThread.join();

		String result = IOUtil.read(new FileInputStream(outputFile), "UTF-8");
		assertEquals("this is a test �", result);
	}

	@Test
	public void testNullParameters()
	{
		try
		{
			new OutputStreamThread(null, null, null);
		}
		catch (Exception e)
		{
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
}
