/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Test;

import com.aptana.core.ShellExecutable;

public class PathUtilTest
{

	@After
	public void tearDown() throws Exception
	{
		ShellExecutable.setPreferenceShellPath(null);
	}
	@Test
	public void testCygwinPath() throws Exception
	{
		ShellExecutable.setPreferenceShellPath(Path.fromOSString("C:\\cygwin\\bin\\sh.exe"));
		String rawPATH = "/usr/local/bin:/usr/bin:/cygdrive/c/Windows/system32:/cygdrive/c/Windows:/cygdrive/c/Windows/System32/Wbem:/cygdrive/c/Windows/System32/WindowsPowerShell/v1.0:/cygdrive/c/apache-ant-1.7.1/bin:/cygdrive/c/RailsInstaller/Git/cmd:/cygdrive/c/RailsInstaller/Ruby1.8.7/bin:/cygdrive/c/Program Files (x86)/CVSNT";
		String actual = PathUtil.convertPATH(rawPATH);
		String expected = "C:\\cygwin\\usr\\local\\bin;C:\\cygwin\\bin;C:\\Windows\\system32;C:\\Windows;C:\\Windows\\System32\\Wbem;C:\\Windows\\System32\\WindowsPowerShell\\v1.0;C:\\apache-ant-1.7.1\\bin;C:\\RailsInstaller\\Git\\cmd;C:\\RailsInstaller\\Ruby1.8.7\\bin;C:\\Program Files (x86)\\CVSNT";
		assertEquals(expected, actual);
	}
	@Test
	public void testNormalWindowsPath() throws Exception
	{
		String rawPATH = "C:\\Windows\\system32;C:\\Windows;C:\\Windows\\System32\\Wbem;C:\\Windows\\System32\\WindowsPowerShell\\v1.0;C:\\apache-ant-1.7.1\\bin;C:\\RailsInstaller\\Git\\cmd;C:\\RailsInstaller\\Ruby1.8.7\\bin;C:\\Program Files (x86)\\CVSNT";
		String actual = PathUtil.convertPATH(rawPATH);
		assertEquals(rawPATH, actual);
	}

}
