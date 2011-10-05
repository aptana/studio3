package com.aptana.core.util;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;

public class ProcessStatusTest extends TestCase
{

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

	public void testSeverityIsErrorWithNonZeroExitCode() throws Exception
	{
		String stdOut = "";
		String stdErr = "stdErr";
		int exitCode = 1;
		ProcessStatus status = new ProcessStatus(exitCode, stdOut, stdErr);
		assertEquals(IStatus.ERROR, status.getSeverity());
		assertFalse(status.isOK());
	}

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
