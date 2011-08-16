/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public class ExecutableUtilTest extends TestCase
{
	private static final String BUNDLE_ID = "com.aptana.core.tests";
	private static final String RESOURCE_DIR = "resources";

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

	public void testFindFileInWorkingDirectory()
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		IPath resourceDirectory = Path.fromOSString(ResourceUtil.resourcePathToString(resourceURL));
		String executableFile = "executableTest";
		List<IPath> location = new ArrayList<IPath>();
		location.add(resourceDirectory);

		IPath path = ExecutableUtil.find(executableFile, false, location, resourceDirectory);
		assertNotNull("Could not find executable file in working directory", path);
	}

	public void testFindFileNotExecutable()
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		IPath workingDirectory = Path.fromOSString(ResourceUtil.resourcePathToString(resourceURL));
		List<IPath> location = new ArrayList<IPath>();
		location.add(workingDirectory);

		IPath path = ExecutableUtil.find("test.js", false, location);
		assertTrue("Found a non-executable file", workingDirectory.append("test.js").toFile().exists() && path == null);
	}

	public void testFindWithNullWorkingDirectory()
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		IPath resourceDirectory = Path.fromOSString(ResourceUtil.resourcePathToString(resourceURL));
		String executableFile = "executableTest";
		List<IPath> location = new ArrayList<IPath>();
		location.add(resourceDirectory);

		IPath path = ExecutableUtil.find(executableFile, false, location, (IPath) null);
		assertNotNull("Could not find executable with valid search location", path);
	}

	public void testFindNullDirectory()
	{
		IPath path = ExecutableUtil.find(null, false, null);
		assertNull("Found a directory from null argument", path);
	}

}
