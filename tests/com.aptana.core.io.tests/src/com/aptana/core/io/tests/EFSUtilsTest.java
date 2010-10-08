/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.io.tests;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.ide.core.io.efs.EFSUtils;

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
		assertEquals("/" + f.getName(), EFSUtils.getRelativePath(EFS.getLocalFileSystem().fromLocalFile( //$NON-NLS-1$
				f.getParentFile()), EFS.getLocalFileSystem().fromLocalFile(f)));

		assertEquals("", EFSUtils.getRelativePath(EFS.getLocalFileSystem().fromLocalFile(f.getParentFile()), EFS //$NON-NLS-1$
				.getLocalFileSystem().fromLocalFile(f.getParentFile())));

		assertNull(EFSUtils.getRelativePath(EFS.getLocalFileSystem().fromLocalFile(f), EFS.getLocalFileSystem()
				.fromLocalFile(f2)));
	}
}
