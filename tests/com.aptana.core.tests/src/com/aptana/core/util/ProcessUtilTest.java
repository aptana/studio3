/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProcessUtilTest
{

	private Mockery context;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		context = null;
	}

	@Test
	public void testProcessResultReturnsExitCodeAndOutputsInProcessStatusObject() throws Exception
	{
		final String stdOutText = "stdout";
		final String stdErrText = "stdout";
		final int exitCode = 0;

		final Process process = context.mock(Process.class);
		context.checking(new Expectations()
		{
			{
				oneOf(process).getInputStream();
				will(returnValue(new ByteArrayInputStream(stdOutText.getBytes())));

				oneOf(process).getErrorStream();
				will(returnValue(new ByteArrayInputStream(stdErrText.getBytes())));

				oneOf(process).getOutputStream();

				oneOf(process).waitFor();
				will(returnValue(exitCode));
			}
		});

		IStatus status = ProcessUtil.processResult(process);
		assertNotNull(status);
		assertTrue(status instanceof ProcessStatus);
		ProcessStatus pStatus = (ProcessStatus) status;
		assertEquals(exitCode, pStatus.getCode());
		assertEquals(stdOutText, pStatus.getStdOut());
		assertEquals(stdErrText, pStatus.getStdErr());
		context.assertIsSatisfied();
	}

	@Test
	public void testProcessResultReturnsNullIfInterruptedExceptionIsThrown() throws Exception
	{
		final String stdOutText = "stdout";
		final String stdErrText = "stdout";

		final Process process = context.mock(Process.class);
		context.checking(new Expectations()
		{
			{
				oneOf(process).getInputStream();
				will(returnValue(new ByteArrayInputStream(stdOutText.getBytes())));

				oneOf(process).getErrorStream();
				will(returnValue(new ByteArrayInputStream(stdErrText.getBytes())));

				oneOf(process).getOutputStream();

				oneOf(process).waitFor();
				will(throwException(new InterruptedException()));
			}
		});

		IStatus status = ProcessUtil.processResult(process);
		assertNull(status);
		context.assertIsSatisfied();
	}

	@Test
	public void testOutputForProcessReturnsProcessStatusMessage() throws Exception
	{
		final String stdOutText = "stdout";
		final String stdErrText = "stdout";
		final int exitCode = 0;

		final Process process = context.mock(Process.class);
		context.checking(new Expectations()
		{
			{
				oneOf(process).getInputStream();
				will(returnValue(new ByteArrayInputStream(stdOutText.getBytes())));

				oneOf(process).getErrorStream();
				will(returnValue(new ByteArrayInputStream(stdErrText.getBytes())));

				oneOf(process).getOutputStream();

				oneOf(process).waitFor();
				will(returnValue(exitCode));
			}
		});

		String output = ProcessUtil.outputForProcess(process);
		assertNotNull(output);
		assertEquals(stdOutText, output);
		context.assertIsSatisfied();
	}

	@Test
	public void testWaitForProcess() throws Exception
	{
		int timeout = 300;
		boolean forceKill = false;
		final int exitCode = 0;

		final Process process = context.mock(Process.class);
		context.checking(new Expectations()
		{
			{
				exactly(2).of(process).waitFor();
				// TODO Is there any way to verify it started the timeout thread?
				will(returnValue(exitCode));
			}
		});

		int result = ProcessUtil.waitForProcess(process, timeout, forceKill);
		assertEquals(exitCode, result);
		context.assertIsSatisfied();
	}

	@Test
	public void testWaitForProcessWithNegativeOneTimeout() throws Exception
	{
		int timeout = -1;
		boolean forceKill = false;
		final int exitCode = 0;

		final Process process = context.mock(Process.class);
		context.checking(new Expectations()
		{
			{
				oneOf(process).waitFor();
				will(returnValue(exitCode));
			}
		});

		int result = ProcessUtil.waitForProcess(process, timeout, forceKill);
		assertEquals(exitCode, result);
		context.assertIsSatisfied();
	}

	@Test
	public void testWaitForProcessWithNegativeTimeout() throws Exception
	{
		int timeout = -100;
		boolean forceKill = false;
		final int exitCode = 0;

		final Process process = context.mock(Process.class);
		context.checking(new Expectations()
		{
			{
				atLeast(1).of(process).waitFor();
				will(returnValue(exitCode));
			}
		});

		int result = ProcessUtil.waitForProcess(process, timeout, forceKill);
		assertEquals(exitCode, result);
		context.assertIsSatisfied();
	}

	@Test
	public void testWaitForProcessPastTimeoutWithForceKillDestroysProcess() throws Exception
	{
		int timeout = 10;
		boolean forceKill = true;
		final int exitCode = 0;

		final Process process = context.mock(Process.class);
		context.checking(new Expectations()
		{
			{
				final Sequence blah = context.sequence("sequence-name");

				oneOf(process).waitFor();
				inSequence(blah);
				will(sleepAndReturn(100000, exitCode));

				oneOf(process).waitFor();
				inSequence(blah);
				will(returnValue(exitCode));

				oneOf(process).destroy();
			}
		});

		int result = ProcessUtil.waitForProcess(process, timeout, forceKill);
		assertEquals(exitCode, result);
		context.assertIsSatisfied();
	}

	private static SleepAndReturnValueAction sleepAndReturn(int sleepTime, Object value)
	{
		return new SleepAndReturnValueAction(sleepTime, value);
	}

	private static class SleepAndReturnValueAction implements Action
	{
		private Object result;
		private int sleepTime;

		public SleepAndReturnValueAction(int sleepTime, Object result)
		{
			this.sleepTime = sleepTime;
			this.result = result;
		}

		public Object invoke(Invocation invocation) throws Throwable
		{
			Thread.sleep(sleepTime);
			return result;
		}

		public void describeTo(Description description)
		{
			description.appendText("sleeps ");
			description.appendValue(sleepTime);
			description.appendText("ms and then returns ");
			description.appendValue(result);
		}
	}
}
