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
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * @author Shalom
 */
public class InputStreamGobblerTest
{
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	/*
	 * Test a gobbler on a process that should not yield any errors.s
	 */
	@Test
	public void testGobblerNoErrors() throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder("java", "-version");
		// Since the -version command outputs to the error stream by default, we ask the process builder to redirect all
		// output to the standard output (just for the test...)
		pb.redirectErrorStream(true);

		Process process = pb.start();
		InputStreamGobbler readerGobbler = new InputStreamGobbler(process.getInputStream(), LINE_SEPARATOR, "UTF-8"); //$NON-NLS-1$
		InputStreamGobbler errorGobbler = new InputStreamGobbler(process.getErrorStream(), LINE_SEPARATOR, null);

		// Check that the current result are null. The gobblers were not started yet.
		assertNull("The stream gobbler had a non-null result before it got strated", readerGobbler.getResult());
		assertNull("The stream gobbler had a non-null result before it got strated", errorGobbler.getResult());

		// Initiate the gobblers
		readerGobbler.start();
		errorGobbler.start();

		// This will wait till the process is done.
		process.waitFor();
		readerGobbler.interrupt();
		errorGobbler.interrupt();
		readerGobbler.join();
		errorGobbler.join();

		String stdout = readerGobbler.getResult();
		String stderr = errorGobbler.getResult();

		assertTrue("Expected process output, but got none", !StringUtil.EMPTY.equals(stdout));
		assertTrue("Expected empty string, but got a non-empty error string", StringUtil.EMPTY.equals(stderr));
	}

	/*
	 * Test a gobbler on a process that should not yield any errors.s
	 */
	@Test
	public void testGobblerWithErrors() throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder("java", "-vers");
		Process process = pb.start();
		InputStreamGobbler readerGobbler = new InputStreamGobbler(process.getInputStream(), LINE_SEPARATOR, "UTF-8"); //$NON-NLS-1$
		InputStreamGobbler errorGobbler = new InputStreamGobbler(process.getErrorStream(), LINE_SEPARATOR, null);

		// Initiate the gobblers
		readerGobbler.start();
		errorGobbler.start();

		// This will wait till the process is done.
		process.waitFor();
		readerGobbler.interrupt();
		errorGobbler.interrupt();
		readerGobbler.join();
		errorGobbler.join();

		String stdout = readerGobbler.getResult();
		String stderr = errorGobbler.getResult();

		assertTrue("Expected empty string, but got a non-empty error string", StringUtil.EMPTY.equals(stdout));
		assertTrue("Expected process output, but got none", !StringUtil.isEmpty(stderr));
	}

	@Test
	public void testBadInitialization_1() throws Exception
	{
		try
		{
			new InputStreamGobbler(null, LINE_SEPARATOR, "UTF-8");
		}
		catch (IllegalArgumentException e)
		{
			return;
		}
		assertTrue("An InputStreamGobbler was created with invalid arguments", false);
	}

	@Test
	public void testBadInitialization_2() throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder("java", "-version");
		Process process = pb.start();
		try
		{
			new InputStreamGobbler(process.getInputStream(), null, "UTF-8");
		}
		catch (IllegalArgumentException e)
		{
			return;
		}
		finally
		{
			process.destroy();
		}
		assertTrue("An InputStreamGobbler was created with invalid arguments", false);
	}

	@Test
	public void testTerminatedProcess() throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder("java", "-version");
		pb.redirectErrorStream(true);
		Process process = pb.start();
		process.waitFor();

		InputStream inputStream = process.getInputStream();
		inputStream.close();

		InputStreamGobbler readerGobbler = new InputStreamGobbler(inputStream, LINE_SEPARATOR, "UTF-8"); //$NON-NLS-1$

		readerGobbler.start();
		readerGobbler.join();

		assertTrue("Expected for an empty result from a InputStreamGobbler that was created on a terminated process",
				StringUtil.EMPTY.equals(readerGobbler.getResult()));
	}
}
