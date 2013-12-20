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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
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

import com.aptana.core.logging.IdeLog;

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
	public void testDoRunWithWorkingDirandCustomEnv() throws Exception
	{
		final List<String> command = new ArrayList<String>();
		final IPath workingDir = FileUtil.getTempDirectory();
		final Map<String, String> env = new HashMap<String, String>();
		env.put("custom_key", "custom_value");

		ProcessUtil pu = new ProcessUtil()
		{
			@Override
			protected Process startProcess(ProcessBuilder processBuilder) throws IOException
			{
				// Verify the command matches expectations
				assertEquals(command, processBuilder.command());
				// verify the working dir is what we expect
				assertEquals(workingDir.toFile(), processBuilder.directory());
				// verify our custom env values got inserted
				assertTrue(processBuilder.environment().containsKey("custom_key"));
				assertEquals("custom_value", processBuilder.environment().get("custom_key"));
				return null;
			}
		};

		pu.doRun(command, workingDir, env);
	}

	@Test
	public void testDoRunWithNullWorkingDirandCustomEnv() throws Exception
	{
		final List<String> command = new ArrayList<String>();
		final IPath workingDir = null;
		final Map<String, String> env = new HashMap<String, String>();
		env.put("custom_key", "custom_value");

		IdeLog.StatusLevel level = IdeLog.getCurrentSeverity();
		IdeLog.setCurrentSeverity(IdeLog.StatusLevel.INFO);

		ProcessUtil pu = new ProcessUtil()
		{
			@Override
			protected Process startProcess(ProcessBuilder processBuilder) throws IOException
			{
				// Verify the command matches expectations
				assertEquals(command, processBuilder.command());
				// verify the working dir is what we expect
				assertNull(processBuilder.directory());
				// verify our custom env values got inserted
				assertTrue(processBuilder.environment().containsKey("custom_key"));
				assertEquals("custom_value", processBuilder.environment().get("custom_key"));
				return null;
			}
		};

		pu.doRun(command, workingDir, env);

		IdeLog.setCurrentSeverity(level);
	}

	@Test
	public void testDoRunWithNullWorkingDirandNoCustomEnv() throws Exception
	{
		final List<String> command = new ArrayList<String>();
		final IPath workingDir = null;
		final Map<String, String> env = null;

		ProcessUtil pu = new ProcessUtil()
		{
			@Override
			protected Process startProcess(ProcessBuilder processBuilder) throws IOException
			{
				// Verify the command matches expectations
				assertEquals(command, processBuilder.command());
				// verify the working dir is what we expect
				assertNull(processBuilder.directory());
				// verify env is system env
				assertFalse(processBuilder.environment().containsKey("custom_key"));
				assertEquals(System.getenv(), processBuilder.environment());
				return null;
			}
		};

		pu.doRun(command, workingDir, env);
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

	@Test
	public void testObfuscation() throws Exception
	{
		List<String> args = CollectionsUtil.newList("binary", "C:\\Users\\QEtester\\", "--password", "password");

		assertEquals("\"binary\" \"C:\\Users\\QEtester\\\" \"--password\" \"**********\"",
				ProcessUtil.getObfuscatedCommandString(args, "password"));

		args = CollectionsUtil.newList("binary", "C:\\Users\\QEtester\\", "--password", "tester");
		assertEquals("\"binary\" \"C:\\Users\\QEtester\\\" \"--password\" \"**********\"",
				ProcessUtil.getObfuscatedCommandString(args, "tester"));

		args = CollectionsUtil.newList("binary", "C:\\Users\\tester\\", "--password", "tester");
		assertEquals("\"binary\" \"C:\\Users\\tester\\\" \"--password\" \"**********\"",
				ProcessUtil.getObfuscatedCommandString(args, "tester"));

	}

	@Test
	public void testObfuscationWithProcess() throws Exception
	{
		final List<String> logs = new ArrayList<String>();
		Map<String, String> environment = CollectionsUtil.newMap(ProcessUtil.TEXT_TO_OBFUSCATE, "password123");

		List<String> args = CollectionsUtil.newList("binary", "/User/cwilliams/path", "--password", "password123");
		new ProcessUtil()
		{
			protected Process startProcess(ProcessBuilder processBuilder) throws IOException
			{
				return null;
			}

			protected boolean isInfoLoggingEnabled()
			{
				return true;
			}

			protected void logInfo(String msg)
			{
				logs.add(msg);
			}

		}.doRun(args, null, environment);

		assertEquals(1, logs.size());
		assertEquals(MessageFormat.format(Messages.ProcessUtil_RunningProcess,
				"\"binary\" \"/User/cwilliams/path\" \"--password\" \"**********\"", null, null), logs.get(0));
	}

	@Test
	public void testObfuscationOfProxy() throws Exception
	{
		final List<String> logs = new ArrayList<String>();
		Map<String, String> environment = CollectionsUtil.newMap(ProcessUtil.TEXT_TO_OBFUSCATE, "cwilliams");

		List<String> args = CollectionsUtil.newList("binary", "/User/cwilliams/path", "--proxy",
				"http://user:cwilliams@1.2.3.4:80");
		new ProcessUtil()
		{
			protected Process startProcess(ProcessBuilder processBuilder) throws IOException
			{
				return null;
			}

			protected boolean isInfoLoggingEnabled()
			{
				return true;
			}

			protected void logInfo(String msg)
			{
				logs.add(msg);
			}

		}.doRun(args, null, environment);

		assertEquals(1, logs.size());
		// Verify we obfuscate only in the proxy, not in the filepath!
		assertEquals(MessageFormat.format(Messages.ProcessUtil_RunningProcess,
				"\"binary\" \"/User/cwilliams/path\" \"--proxy\" \"http://user:**********@1.2.3.4:80\"", null, null),
				logs.get(0));
	}

	@Test
	public void testObfuscationOfKeyValuePair() throws Exception
	{
		final List<String> logs = new ArrayList<String>();
		Map<String, String> environment = CollectionsUtil.newMap(ProcessUtil.TEXT_TO_OBFUSCATE, "cwilliams");

		List<String> args = CollectionsUtil.newList("binary", "/User/cwilliams/path", "password=cwilliams");
		new ProcessUtil()
		{
			protected Process startProcess(ProcessBuilder processBuilder) throws IOException
			{
				return null;
			}

			protected boolean isInfoLoggingEnabled()
			{
				return true;
			}

			protected void logInfo(String msg)
			{
				logs.add(msg);
			}

		}.doRun(args, null, environment);

		assertEquals(1, logs.size());
		// Verify we obfuscate only in the key-value pair, not in the filepath!
		assertEquals(MessageFormat.format(Messages.ProcessUtil_RunningProcess,
				"\"binary\" \"/User/cwilliams/path\" \"password=**********\"", null, null), logs.get(0));
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
