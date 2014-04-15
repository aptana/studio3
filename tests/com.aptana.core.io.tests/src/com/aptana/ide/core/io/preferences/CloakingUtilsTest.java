/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io.preferences;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;

import com.aptana.core.util.FileUtil;

public class CloakingUtilsTest
{

	@Test
	public void testFile() throws IOException
	{
		File file = FileUtil.createTempFile("test", "txt");
		IFileStore fileStore = EFS.getLocalFileSystem().fromLocalFile(file);

		CloakingUtils.addCloakFileType(file.getName());
		assertTrue("the file should be cloaked but is not", CloakingUtils.isFileCloaked(fileStore));

		CloakingUtils.removeCloakFileType(file.getName());
		assertFalse("the file should not be cloaked but is", CloakingUtils.isFileCloaked(fileStore));
	}

	@Test
	public void testDirectory() throws IOException
	{
		File dir = new File(FileUtil.getTempDirectory().toOSString(), "cloaking");
		dir.mkdir();
		dir.deleteOnExit();
		IFileStore fileStore = EFS.getLocalFileSystem().fromLocalFile(dir);

		CloakingUtils.addCloakFileType(dir.getName());
		assertTrue("the directory should be cloaked but is not", CloakingUtils.isFileCloaked(fileStore));

		CloakingUtils.removeCloakFileType(dir.getName());
		assertFalse("the directory should not be cloaked but is", CloakingUtils.isFileCloaked(fileStore));
	}

	@Test
	public void testRegex() throws IOException
	{
		File dir = new File(FileUtil.getTempDirectory().toOSString(), "cloaking");
		dir.mkdir();
		dir.deleteOnExit();
		File file = File.createTempFile("test", "txt", dir);
		file.deleteOnExit();
		IFileStore fileStore = EFS.getLocalFileSystem().fromLocalFile(file);

		String regex = "/.*\\/cloaking\\/.*txt/";
		CloakingUtils.addCloakFileType(regex);
		assertTrue("the file should be cloaked but is not", CloakingUtils.isFileCloaked(fileStore));

		CloakingUtils.removeCloakFileType(regex);
		assertFalse("the directory should not be cloaked but is", CloakingUtils.isFileCloaked(fileStore));
	}
}
