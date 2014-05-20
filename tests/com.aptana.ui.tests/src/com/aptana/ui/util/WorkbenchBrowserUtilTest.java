package com.aptana.ui.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.IProcessRunner;
import com.aptana.core.util.ProcessStatus;

public class WorkbenchBrowserUtilTest
{

	private WorkbenchBrowserUtil util;
	private Mockery context;
	private IProcessRunner runner;
	private IWorkbenchBrowserSupport support;
	private boolean isMac = false;
	private boolean isWindows = false;
	private boolean programLaunchSuccessful = false;

	@Before
	public void setUp() throws Exception
	{
		isMac = false;
		isWindows = false;
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		runner = context.mock(IProcessRunner.class);
		support = context.mock(IWorkbenchBrowserSupport.class);
		util = new WorkbenchBrowserUtil(runner, support)
		{
			protected boolean isMac()
			{
				return isMac;
			}

			protected boolean isWindows()
			{
				return isWindows;
			}

			@Override
			protected boolean launchProgram(URL url)
			{
				return programLaunchSuccessful;
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		context = null;
		runner = null;
		support = null;
		util = null;
	}

	@Test
	public void testLaunchBrowserByCommandMac() throws MalformedURLException
	{
		isMac = true;
		context.checking(new Expectations()
		{
			{
				oneOf(runner).runInBackground("open", "http://example.com");
				will(returnValue(Status.OK_STATUS));
			}
		});
		util.doLaunchBrowserByCommand(new URL("http://example.com"));
		context.assertIsSatisfied();
	}

	@Test
	public void testLaunchBrowserByCommandWindows() throws MalformedURLException
	{
		isWindows = true;
		context.checking(new Expectations()
		{
			{
				oneOf(runner).runInBackground("reg", "query", "HKEY_CLASSES_ROOT\\http\\shell\\open\\command");
				will(returnValue(new ProcessStatus(
						0,
						"\nHKEY_CLASSES_ROOT\\http\\shell\\open\\command\n    (Default)    REG_SZ    \"C:\\Program Files\\Internet Explorer\\iexplore.exe\" %1\n    DelegateExecute    REG_SZ    {17FE9752-0B5A-4665-84CD-569794602F5C}",
						null)));

				oneOf(runner).runInBackground("C:\\Program Files\\Internet Explorer\\iexplore.exe",
						"http://example.com");
				will(returnValue(Status.OK_STATUS));
			}
		});
		util.doLaunchBrowserByCommand(new URL("http://example.com"));
		context.assertIsSatisfied();
	}

	@Test
	public void testLaunchBrowserByCommandWindowsWithChrome() throws MalformedURLException
	{
		isWindows = true;
		context.checking(new Expectations()
		{
			{
				oneOf(runner).runInBackground("reg", "query", "HKEY_CLASSES_ROOT\\http\\shell\\open\\command");
				will(returnValue(new ProcessStatus(
						0,
						"\n	HKEY_CLASSES_ROOT\\http\\shell\\open\\command\n    (Default)    REG_SZ    \"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe\" -- \"%1\"\n",
						null)));

				oneOf(runner).runInBackground("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe", "--",
						"http://example.com");
				will(returnValue(Status.OK_STATUS));
			}
		});
		util.doLaunchBrowserByCommand(new URL("http://example.com"));
		context.assertIsSatisfied();
	}

	@Test
	public void testLaunchBrowserByCommandOther() throws MalformedURLException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(runner).runInBackground("xdg-open", "http://example.com");
				will(returnValue(Status.OK_STATUS));
			}
		});
		util.doLaunchBrowserByCommand(new URL("http://example.com"));
		context.assertIsSatisfied();
	}

	@Test
	public void testLaunchBrowserByCommandUsesProgramLaunchSuccessfully() throws MalformedURLException
	{
		programLaunchSuccessful = true;
		context.checking(new Expectations()
		{
			{
				never(runner).runInBackground("xdg-open", "http://example.com");
			}
		});
		util.doLaunchBrowserByCommand(new URL("http://example.com"));
		context.assertIsSatisfied();
	}

}
