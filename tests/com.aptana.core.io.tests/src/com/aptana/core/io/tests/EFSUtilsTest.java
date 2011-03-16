/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.io.tests;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.aptana.core.io.LocalConnectionPoint;
import com.aptana.core.io.efs.EFSUtils;

import junit.framework.TestCase;

public class EFSUtilsTest extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testGetAbsolutePath() throws IOException, CoreException
	{
		File f = File.createTempFile("test", "txt"); //$NON-NLS-1$ //$NON-NLS-2$
		LocalConnectionPoint lcp = new LocalConnectionPoint(Path.fromOSString(f.getAbsolutePath()));
		assertEquals(f.getAbsolutePath(), EFSUtils.getAbsolutePath(lcp.getRoot()));
	}

	public void testGetPath() throws IOException, CoreException
	{
		File f = File.createTempFile("test", "txt"); //$NON-NLS-1$ //$NON-NLS-2$
		LocalConnectionPoint lcp = new LocalConnectionPoint(Path.fromOSString(f.getAbsolutePath()));
		assertEquals(f.getAbsolutePath(), EFSUtils.getAbsolutePath(lcp.getRoot()));
	}

	public void testGetRelativePath() throws IOException, CoreException
	{
		File f = File.createTempFile("test", "txt"); //$NON-NLS-1$ //$NON-NLS-2$
		File f2 = File.createTempFile("test", "txt"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(f.getName(), EFSUtils.getRelativePath(EFS.getLocalFileSystem().fromLocalFile(
				f.getParentFile()), EFS.getLocalFileSystem().fromLocalFile(f)).toPortableString());

		assertNull(EFSUtils.getRelativePath(EFS.getLocalFileSystem().fromLocalFile(f.getParentFile()), EFS
				.getLocalFileSystem().fromLocalFile(f.getParentFile())));

		assertNull(EFSUtils.getRelativePath(EFS.getLocalFileSystem().fromLocalFile(f), EFS.getLocalFileSystem()
				.fromLocalFile(f2)));
	}
}
