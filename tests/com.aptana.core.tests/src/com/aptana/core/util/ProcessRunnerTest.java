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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProcessRunnerTest
{
	private ProcessBuilder builder;
	private ProcessRunner runner;
	private Mockery context;
	private IPath userHome;

	@Before
	public void setup()
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		runner = new ProcessRunner()
		{
			@Override
			protected ProcessBuilder createProcessBuilder(List<String> command)
			{
				return builder = new ProcessBuilder(command);
			}

			@Override
			protected Process startProcess(ProcessBuilder processBuilder) throws IOException
			{
				return null;
			}
		};

		String path = PlatformUtil.expandEnvironmentStrings("~"); //$NON-NLS-1$
		userHome = Path.fromOSString(path);
	}

	@After
	public void tearDown() throws Exception
	{
		userHome = null;
		builder = null;
		runner = null;
		context = null;
	}

	@Test
	public void testRunWithNoWorkingDirectoryOrEnvironment() throws IOException, CoreException
	{
		final List<String> args = CollectionsUtil.newList("sudo", "-S", "--", "echo", "SUCCESS");
		runner.run(args.toArray(new String[args.size()]));
		assertEquals(args, builder.command()); // Verify it sets the args properly
		assertNull(builder.directory());
		context.assertIsSatisfied();
	}

	@Test
	public void testRunWithWorkingDirectoryNoEnvironment() throws IOException, CoreException
	{
		final List<String> args = CollectionsUtil.newList("sudo", "-S", "--", "echo", "SUCCESS");
		runner.run(userHome, args.toArray(new String[args.size()]));
		assertEquals(args, builder.command()); // Verify it sets the args properly
		assertEquals(userHome.toFile(), builder.directory()); // Make sure working dir got set
		assertEquals(System.getenv(), builder.environment());
		context.assertIsSatisfied();
	}

	@Test
	public void testRunWithNoWorkingDirectoryWithEnvironment() throws IOException, CoreException
	{
		final List<String> args = CollectionsUtil.newList("sudo", "-S", "--", "echo", "SUCCESS");
		final Map<String, String> env = CollectionsUtil.newMap("key", "value");
		runner.run(null, env, args.toArray(new String[args.size()]));
		assertEquals(args, builder.command()); // Verify it sets the args properly
		assertNull(builder.directory());
		assertTrue(builder.environment().containsKey("key"));
		assertTrue(builder.environment().containsValue("value"));
		assertEquals("value", builder.environment().get("key"));
		context.assertIsSatisfied();
	}

	@Test
	public void testRunWithNoWorkingDirectoryWithEnvironmentSetToRedirect() throws IOException, CoreException
	{
		final List<String> args = CollectionsUtil.newList("sudo", "-S", "--", "echo", "SUCCESS");
		final Map<String, String> env = CollectionsUtil.newMap("key", "value", IProcessRunner.REDIRECT_ERROR_STREAM,
				"true");
		runner.run(null, env, args.toArray(new String[args.size()]));
		assertEquals(args, builder.command()); // Verify it sets the args properly
		assertTrue(builder.environment().containsKey("key"));
		assertTrue(builder.environment().containsValue("value"));
		assertEquals("value", builder.environment().get("key"));
		assertTrue(builder.redirectErrorStream()); // our special flag should set the redirect flag
		context.assertIsSatisfied();
	}

	@Test
	public void testObfuscation() throws Exception
	{
		List<String> args = CollectionsUtil.newList("binary", "C:\\Users\\QEtester\\", "--password", "password");
		assertEquals("\"binary\" \"C:\\Users\\QEtester\\\" \"--password\" \"**********\"",
				new ProcessRunner().getObfuscatedCommandString(args, "password"));

		args = CollectionsUtil.newList("binary", "C:\\Users\\QEtester\\", "--password", "tester");
		assertEquals("\"binary\" \"C:\\Users\\QEtester\\\" \"--password\" \"**********\"",
				new ProcessRunner().getObfuscatedCommandString(args, "tester"));

		args = CollectionsUtil.newList("binary", "C:\\Users\\tester\\", "--password", "tester");
		assertEquals("\"binary\" \"C:\\Users\\tester\\\" \"--password\" \"**********\"",
				new ProcessRunner().getObfuscatedCommandString(args, "tester"));

		args = CollectionsUtil.newList("binary", "C:\\Users\\tester\\", "--password", "passwordWith@");
		assertEquals("\"binary\" \"C:\\Users\\tester\\\" \"--password\" \"**********\"",
				new ProcessRunner().getObfuscatedCommandString(args, "passwordWith@"));

		args = CollectionsUtil.newList("binary", "C:\\Users\\tester\\", "myuser:mypassword@host:port", "command");
		assertEquals("\"binary\" \"C:\\Users\\tester\\\" \"myuser:**********@host:port\" \"command\"",
				new ProcessRunner().getObfuscatedCommandString(args, "mypassword"));

		args = CollectionsUtil.newList("binary", "C:\\Users\\tester\\", "myuser:passwordWith@@host:port", "command");
		assertEquals("\"binary\" \"C:\\Users\\tester\\\" \"myuser:**********@host:port\" \"command\"",
				new ProcessRunner().getObfuscatedCommandString(args, "passwordWith@"));

		args = CollectionsUtil.newList("binary", "C:\\Users\\tester\\", "--password=passwordWith@", "command");
		assertEquals("\"binary\" \"C:\\Users\\tester\\\" \"--password=**********\" \"command\"",
				new ProcessRunner().getObfuscatedCommandString(args, "passwordWith@"));
	}

	@Test
	public void testObfuscationWithProcess() throws Exception
	{
		final List<String> args = CollectionsUtil
				.newList("binary", "/User/cwilliams/path", "--password", "password123");
		final Map<String, String> env = CollectionsUtil.newMap(IProcessRunner.TEXT_TO_OBFUSCATE, "password123");

		final List<String> logs = new ArrayList<String>();

		runner = new ProcessRunner()
		{
			@Override
			protected ProcessBuilder createProcessBuilder(List<String> command)
			{
				return builder = new ProcessBuilder(command);
			}

			@Override
			protected Process startProcess(ProcessBuilder processBuilder) throws IOException
			{
				return null;
			}

			@Override
			protected boolean isInfoLoggingEnabled()
			{
				return true;
			}

			@Override
			protected void logInfo(String msg)
			{
				logs.add(msg);
			}
		};

		runner.run(null, env, args.toArray(new String[args.size()]));
		assertEquals(args, builder.command()); // Verify it sets the args properly
		context.assertIsSatisfied();

		assertEquals(1, logs.size());
		assertEquals(MessageFormat.format(Messages.ProcessUtil_RunningProcess,
				"\"binary\" \"/User/cwilliams/path\" \"--password\" \"**********\"", null, null), logs.get(0));
	}

	@Test
	public void testObfuscationOfProxy() throws Exception
	{
		final List<String> logs = new ArrayList<String>();
		Map<String, String> environment = CollectionsUtil.newMap(IProcessRunner.TEXT_TO_OBFUSCATE, "cwilliams");
		List<String> args = CollectionsUtil.newList("binary", "/User/cwilliams/path", "--proxy",
				"http://user:cwilliams@1.2.3.4:80");

		runner = new ProcessRunner()
		{
			@Override
			protected ProcessBuilder createProcessBuilder(List<String> command)
			{
				return builder = new ProcessBuilder(command);
			}

			@Override
			protected Process startProcess(ProcessBuilder processBuilder) throws IOException
			{
				return null;
			}

			@Override
			protected boolean isInfoLoggingEnabled()
			{
				return true;
			}

			@Override
			protected void logInfo(String msg)
			{
				logs.add(msg);
			}
		};

		runner.run(null, environment, args.toArray(new String[args.size()]));
		assertEquals(args, builder.command()); // Verify it sets the args properly
		context.assertIsSatisfied();

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
		Map<String, String> environment = CollectionsUtil.newMap(IProcessRunner.TEXT_TO_OBFUSCATE, "cwilliams");
		List<String> args = CollectionsUtil.newList("binary", "/User/cwilliams/path", "password=cwilliams");
		runner = new ProcessRunner()
		{
			@Override
			protected ProcessBuilder createProcessBuilder(List<String> command)
			{
				return builder = new ProcessBuilder(command);
			}

			@Override
			protected Process startProcess(ProcessBuilder processBuilder) throws IOException
			{
				return null;
			}

			@Override
			protected boolean isInfoLoggingEnabled()
			{
				return true;
			}

			@Override
			protected void logInfo(String msg)
			{
				logs.add(msg);
			}
		};

		runner.run(null, environment, args.toArray(new String[args.size()]));
		assertEquals(args, builder.command()); // Verify it sets the args properly
		context.assertIsSatisfied();

		assertEquals(1, logs.size());
		// Verify we obfuscate only in the key-value pair, not in the filepath!
		assertEquals(MessageFormat.format(Messages.ProcessUtil_RunningProcess,
				"\"binary\" \"/User/cwilliams/path\" \"password=**********\"", null, null), logs.get(0));
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

		IStatus status = runner.processResult(process);
		assertNotNull(status);
		assertTrue(status instanceof ProcessStatus);
		ProcessStatus pStatus = (ProcessStatus) status;
		assertEquals(exitCode, pStatus.getCode());
		assertEquals(stdOutText, pStatus.getStdOut());
		assertEquals(stdErrText, pStatus.getStdErr());
		context.assertIsSatisfied();
	}
}
