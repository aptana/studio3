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
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public class ZipUtilTest extends TestCase
{

	private static final String BUNDLE_ID = "com.aptana.core.tests";
	private static final String TEST_ZIP = "test.zip";
	private static final String STREAM_TEST_ZIP = "streamtest.zip";
	private static final String RESOURCE_DIR = "resources";
	private static final HashSet<String> FILES = new HashSet<String>(Arrays.asList("test.haml", "test.erb"));

	public void testUnzipFile() throws IOException
	{
		URL resourceURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		IPath resourcePath = Path.fromOSString(ResourceUtil.resourcePathToString(resourceURL));
		File newZip = new File(resourcePath.toOSString(), "test");

		ZipUtil.extract(resourcePath.append(TEST_ZIP).toFile(), resourcePath.toFile(), new NullProgressMonitor());

		assertNotNull("Failed to extract zip", newZip);

		if (newZip.isDirectory())
		{
			File[] files = newZip.listFiles();
			assertTrue("Unzipped contents to not match expected number of files", files.length == 4);

			for (File file : files)
			{
				if (file.isDirectory())
				{
					// test2 folder should have 1 file in it
					if (file.getName().equals("test2"))
					{
						assertTrue(file.list().length == 1);
					}
					continue;
				}
				assertTrue("Unexpected file: " + file + " in unzipped contents", FILES.contains(file.getName()));
			}
		}

		// remove the contents after we are done with the test

		FileUtil.deleteRecursively(newZip);
	}

	public void testOpenEntry() throws IOException
	{
		URL zipURL = Platform.getBundle(BUNDLE_ID).getEntry(RESOURCE_DIR);
		IPath resourcePath = Path.fromOSString(ResourceUtil.resourcePathToString(zipURL));
		IPath zipPath = resourcePath.append(STREAM_TEST_ZIP);
		InputStream stream;
		File zipFile = zipPath.toFile();

		// Open entry should return null when it can't find the specified file
		assertNull(ZipUtil.openEntry(zipFile, Path.fromOSString("test.haml")));

		stream = ZipUtil.openEntry(zipFile, Path.fromOSString("streamtest"));

		String test = IOUtil.read(stream);

		assertTrue(test.equals("this is a test\n\n"));
	}

}
