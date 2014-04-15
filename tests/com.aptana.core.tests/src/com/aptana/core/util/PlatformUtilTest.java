/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;

import com.aptana.core.util.PlatformUtil.ProcessItem;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("nls")
public class PlatformUtilTest
{

	@Test
	public void testGetRunningProcesses()
	{
		ProcessItem[] processes = PlatformUtil.getRunningProcesses();
		assertTrue(processes.length > 0);
		for (ProcessItem i : processes)
		{
			assertTrue(i.getPid() > 0);
			assertTrue(i.getExecutableName().trim().length() > 0);
			assertNotNull(i.toString());
		}
	}

	@Test
	public void testGetRunningChildProcesses() throws IOException
	{
		String cmd = Platform.OS_WIN32.equals(Platform.getOS()) ? "cmd.exe" : (Platform.OS_MACOSX.equals(Platform
				.getOS()) ? "sleep 5s" : "sh");
		String[] command = Platform.OS_WIN32.equals(Platform.getOS()) ? new String[] { "cmd.exe", "/C", "sleep 5s" }
				: new String[] { "sh", "-c", "sleep 5s" };
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.start();
		ProcessItem[] processes = PlatformUtil.getRunningChildProcesses();
		assertTrue(processes.length > 0);
		boolean passed = false;
		for (ProcessItem i : processes)
		{
			String lowerCaseProcessName = i.getExecutableName().toLowerCase();
			if (passed = lowerCaseProcessName.endsWith(cmd))
			{
				break;
			}
			// Sometimes linux shows "[sh]" as process name
			if ("sh".equals(cmd))
			{
				if (passed = lowerCaseProcessName.endsWith("[sh]"))
				{
					break;
				}
			}
		}
		assertTrue("Expected child process \"" + cmd + "\" not found in " + Arrays.asList(processes).toString(), passed);
	}

	@Test
	public void testKillProcesses() throws IOException
	{
		String cmd = Platform.OS_WIN32.equals(Platform.getOS()) ? "cmd.exe" : (Platform.OS_MACOSX.equals(Platform
				.getOS()) ? "sleep 5s" : "sh");
		String[] command = Platform.OS_WIN32.equals(Platform.getOS()) ? new String[] { "cmd.exe", "/C", "sleep 5s" }
				: new String[] { "sh", "-c", "sleep 5s" };
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.start();
		ProcessItem[] processes = PlatformUtil.getRunningChildProcesses();
		assertTrue(processes.length > 0);
		int pid = 0;

		for (ProcessItem i : processes)
		{
			String lowerCaseProcessName = i.getExecutableName().toLowerCase();
			if (lowerCaseProcessName.endsWith(cmd))
			{
				pid = i.getPid();
				break;
			}
			// Sometimes linux shows "[sh]" as process name
			if ("sh".equals(cmd) && lowerCaseProcessName.endsWith("[sh]"))
			{
				pid = i.getPid();
				break;
			}
		}
		assertTrue("Expected child process \"" + cmd + "\" not found in " + Arrays.asList(processes).toString(),
				pid > 0);
		PlatformUtil.killProcess(pid);
		processes = PlatformUtil.getRunningChildProcesses();
		for (ProcessItem i : processes)
		{
			assertNotSame(MessageFormat.format("Expected process {0} did not terminate on kill command", cmd), pid,
					i.getPid());
		}
	}

	@Test
	public void testExpandEnvironmentStrings()
	{
		assertEquals("abc/d", PlatformUtil.expandEnvironmentStrings("abc/d"));
		expandAndCompareEnvironmentStrings("~", "/test");
		expandAndCompareEnvironmentStrings(PlatformUtil.HOME_DIRECTORY, "/test");
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			expandAndCompareEnvironmentStrings(PlatformUtil.DESKTOP_DIRECTORY, "/test");
			expandAndCompareEnvironmentStrings(PlatformUtil.DESKTOP_DIRECTORY, "/test");
			expandAndCompareEnvironmentStrings(PlatformUtil.COMMON_APPDATA, "/test");
			expandAndCompareEnvironmentStrings(PlatformUtil.LOCAL_APPDATA, "/test");
		}
		else if (Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			expandAndCompareEnvironmentStrings(PlatformUtil.DESKTOP_DIRECTORY, "/test");
			expandAndCompareEnvironmentStrings(PlatformUtil.DOCUMENTS_DIRECTORY, "/test");
		}
	}

	private void expandAndCompareEnvironmentStrings(String token, String path)
	{
		String expanded = PlatformUtil.expandEnvironmentStrings(token + path);
		assertNotSame(token + path, expanded);
		assertTrue(expanded.endsWith(path));
	}

}
