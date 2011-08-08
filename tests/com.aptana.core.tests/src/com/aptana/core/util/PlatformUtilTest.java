/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;

import com.aptana.core.util.PlatformUtil.ProcessItem;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("nls")
public class PlatformUtilTest extends TestCase {

	public void testGetRunningProcesses() {
		ProcessItem[] processes = PlatformUtil.getRunningProcesses();
		assertTrue(processes.length > 0);
		for (ProcessItem i : processes) {
			assertTrue(i.getPid() > 0);
			assertTrue(i.getExecutableName().trim().length() > 0);
			i.toString();
		}
	}

	public void testGetRunningChildProcesses() throws IOException {
		String cmd = Platform.OS_WIN32.equals(Platform.getOS()) ? "cmd.exe" : "sleep 5s";
		String[] command = Platform.OS_WIN32.equals(Platform.getOS()) ? new String[] { "cmd.exe", "/C", "sleep 5s" }
				: new String[] { "sh", "-c", "sleep 5s" };
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.start();
		ProcessItem[] processes = PlatformUtil.getRunningChildProcesses();
		assertTrue(processes.length > 0);
		boolean passed = false;
		for (ProcessItem i : processes) {
			if (passed = i.getExecutableName().toLowerCase().endsWith(cmd)) {
				break;
			}
		}
		assertTrue(passed);
	}

	public void testKillProcesses() throws IOException {
		String cmd = Platform.OS_WIN32.equals(Platform.getOS()) ? "cmd.exe" : "sleep 5s";
		String[] command = Platform.OS_WIN32.equals(Platform.getOS()) ? new String[] { "cmd.exe", "/C", "sleep 5s" }
				: new String[] { "sh", "-c", "sleep 5s" };
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.start();
		ProcessItem[] processes = PlatformUtil.getRunningChildProcesses();
		assertTrue(processes.length > 0);
		int pid = 0;
		for (ProcessItem i : processes) {
			if (i.getExecutableName().toLowerCase().endsWith(cmd)) {
				pid = i.getPid();
				break;
			}
		}
		assertTrue(pid > 0);
		PlatformUtil.killProcess(pid);
		processes = PlatformUtil.getRunningChildProcesses();
		for (ProcessItem i : processes) {
			assertNotSame(pid, i.getPid());
		}
	}

	public void testExpandEnvironmentStrings() {
		assertEquals("abc/d", PlatformUtil.expandEnvironmentStrings("abc/d"));
		expandAndCompareEnvironmentStrings("~", "/test");
		expandAndCompareEnvironmentStrings(PlatformUtil.HOME_DIRECTORY, "/test");
		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			expandAndCompareEnvironmentStrings(PlatformUtil.DESKTOP_DIRECTORY, "/test");
			expandAndCompareEnvironmentStrings(PlatformUtil.DESKTOP_DIRECTORY, "/test");
			expandAndCompareEnvironmentStrings(PlatformUtil.COMMON_APPDATA, "/test");
			expandAndCompareEnvironmentStrings(PlatformUtil.LOCAL_APPDATA, "/test");
		}
		else if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			expandAndCompareEnvironmentStrings(PlatformUtil.DESKTOP_DIRECTORY, "/test");
			expandAndCompareEnvironmentStrings(PlatformUtil.DOCUMENTS_DIRECTORY, "/test");
		}
	}

	private void expandAndCompareEnvironmentStrings(String token, String path) {
		String expanded = PlatformUtil.expandEnvironmentStrings(token+path);
		assertNotSame(token+path, expanded);
		assertTrue(expanded.endsWith(path));
	}

}
