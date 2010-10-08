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
import java.net.URI;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.aptana.ide.core.io.efs.WorkspaceFileSystem;

public class WorkspaceFileSystemTest extends TestCase {

	protected IFileSystem wfs = null;
	protected IPath location;
	
	protected void setUp() throws Exception {
		super.setUp();
		wfs = WorkspaceFileSystem.getInstance();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		location = root.getLocation();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAttributes() {
		wfs.attributes();
	}

	public void testCanDelete() throws IOException {
		assertTrue(wfs.canDelete());
	}

	public void testCanWrite() throws IOException {
		assertTrue(wfs.canWrite());
	}

	public void testIsCaseSensitive() {
		// This is going to be OS-specific
		//assertFalse(wfs.isCaseSensitive());
	}

	public void testGetInstance() {
		assertNotNull(wfs);
	}

	public void testFromLocalFileFile() throws IOException {
		File baseTempFile = File.createTempFile("test", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
		IFileStore fs = wfs.fromLocalFile(baseTempFile);
		assertNull(fs); // File from temporary location will not be in workspace
		
	}

	public void testGetStoreIPath() throws IOException, CoreException {
		IFileStore fs = wfs.getStore(location);

		// Workspace file has workspace:/ protocol and trailing '/'
		URI uri = fs.toURI();
		String replaced = uri.toString().replaceAll("workspace:", "file:") + "/"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		assertEquals(location.toFile().toURI().toString(), replaced);
	}

	public void testGetStoreURI() throws IOException, CoreException {
		IFileStore fs = wfs.getStore(location.toFile().toURI());

		// Workspace file has workspace:/ protocol
		URI uri = fs.toURI();
		String replaced = uri.toString().replaceAll("workspace:", "file:"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(location.toFile().toURI().toString(), replaced);
	}

}
