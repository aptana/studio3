/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public class ExecutableUtilTest extends TestCase
{
	private static final String tempDir = System.getProperty("java.io.tmpdir");

	public void testFindExecutableFile()
	{
		IPath path;

		if (!Platform.OS_WIN32.equals(Platform.getOS()))
		{
			path = ExecutableUtil.find("bash", false, null);
			assertNotNull("Could not find bash executable", path);
		}
		else
		{
			path = ExecutableUtil.find("cmd", true, null);
			assertNotNull("Could not find cmd executable", path);
		}
	}

	public void testTestingNullExecutable()
	{
		assertFalse(ExecutableUtil.isExecutable(null));
	}

	public void testInvalidExecutable()
	{
		assertNull(ExecutableUtil.find("invalid_executable", false, null));
	}

	public void testFindFileInWorkingDirectory() throws IOException, InterruptedException
	{
		String executableFileName = "executableTest";
		File executableFile = new File(tempDir, executableFileName);
		if (!executableFile.exists())
		{
			executableFile.createNewFile();
		}

		if (!Platform.OS_WIN32.equals(Platform.getOS()))
		{
			Runtime.getRuntime().exec(new String[] { "chmod", "755", executableFile.getAbsolutePath() }).waitFor(); //$NON-NLS-1$
		}

		List<IPath> location = new ArrayList<IPath>();
		IPath executablePath = new Path(executableFile.getAbsolutePath());
		IPath tempDirectoryPath = executablePath.removeLastSegments(1);
		location.add(tempDirectoryPath);

		IPath path = ExecutableUtil.find(executableFileName, false, location, tempDirectoryPath);

		executableFile.delete();
		assertNotNull("Could not find executable file in working directory", path);
	}

	public void testFindFileNotExecutable() throws IOException
	{
		String executableFileName = "executableTest";
		File executableFile = new File(tempDir, executableFileName);
		if (!executableFile.exists())
		{
			executableFile.createNewFile();
		}

		executableFile.setReadOnly();
		List<IPath> location = new ArrayList<IPath>();
		IPath executablePath = new Path(executableFile.getAbsolutePath());
		location.add(executablePath.removeLastSegments(1));

		IPath path = ExecutableUtil.find(executableFileName, false, location, (IPath) null);
		executableFile.delete();

		assertNull("Found a non-executable file", path);

	}

	public void testFindWithNullWorkingDirectory() throws IOException, InterruptedException
	{
		String executableFileName = "executableTest";
		File executableFile = new File(tempDir, executableFileName);
		if (!executableFile.exists())
		{
			executableFile.createNewFile();
		}

		if (!Platform.OS_WIN32.equals(Platform.getOS()))
		{
			Runtime.getRuntime().exec(new String[] { "chmod", "755", executableFile.getAbsolutePath() }).waitFor(); //$NON-NLS-1$
		}

		List<IPath> location = new ArrayList<IPath>();
		IPath executablePath = new Path(executableFile.getAbsolutePath());
		location.add(executablePath.removeLastSegments(1));

		IPath path = ExecutableUtil.find(executableFileName, false, location, (IPath) null);

		executableFile.delete();
		assertNotNull("Could not find executable with valid search location", path);
	}

	public void testFindNullDirectory()
	{
		IPath path = ExecutableUtil.find(null, false, null);
		assertNull("Found a directory from null argument", path);
	}

}
