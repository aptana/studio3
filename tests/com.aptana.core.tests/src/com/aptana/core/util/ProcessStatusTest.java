/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IStatus;
import org.junit.Test;

public class ProcessStatusTest
{
	@Test
	public void testGetMessageReturnsStdErrWithEmptyStdOutAndNonZeroExitCode() throws Exception
	{
		String stdOut = "";
		String stdErr = "stdErr";
		int exitCode = 1;
		ProcessStatus status = new ProcessStatus(exitCode, stdOut, stdErr);
		assertEquals(stdErr, status.getMessage());
		assertEquals(exitCode, status.getCode());
		assertEquals(stdErr, status.getStdErr());
		assertEquals(stdOut, status.getStdOut());
	}

	@Test
	public void testGetMessageReturnsStdOutWithNonEmptyStdOutAndNonZeroExitCode() throws Exception
	{
		String stdOut = "Some output";
		String stdErr = "stdErr";
		int exitCode = 1;
		ProcessStatus status = new ProcessStatus(exitCode, stdOut, stdErr);
		assertEquals(stdOut, status.getMessage());
		assertEquals(exitCode, status.getCode());
		assertEquals(stdErr, status.getStdErr());
		assertEquals(stdOut, status.getStdOut());
	}

	@Test
	public void testGetMessageStripTrailingNewline() throws Exception
	{
		String stdOut = "Some output\n";
		String stdErr = "stdErr";
		int exitCode = 0;
		ProcessStatus status = new ProcessStatus(exitCode, stdOut, stdErr);
		assertEquals("Some output", status.getMessage());
		assertEquals(exitCode, status.getCode());
		assertEquals(stdErr, status.getStdErr());
		assertEquals(stdOut, status.getStdOut());
	}

	@Test
	public void testSeverityIsErrorWithNonZeroExitCode() throws Exception
	{
		String stdOut = "";
		String stdErr = "stdErr";
		int exitCode = 1;
		ProcessStatus status = new ProcessStatus(exitCode, stdOut, stdErr);
		assertEquals(IStatus.ERROR, status.getSeverity());
		assertFalse(status.isOK());
	}

	@Test
	public void testSeverityIsOKWithZeroExitCode() throws Exception
	{
		String stdOut = "";
		String stdErr = "stdErr";
		int exitCode = 0;
		ProcessStatus status = new ProcessStatus(exitCode, stdOut, stdErr);
		assertEquals(IStatus.OK, status.getSeverity());
		assertTrue(status.isOK());
	}
}
