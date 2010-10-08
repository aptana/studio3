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
package com.aptana.core.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;

/**
 * @author Max Stepanov
 */
public class LocalFileStorage extends UniformResourceStorage {

	private File file;
	/**
	 * @param file 
	 * 
	 */
	public LocalFileStorage( File file ) {
		super();
		this.file = file;
	}

	/**
	 * @see com.aptana.ide.core.resources.UniformResourceStorage#getURI()
	 */
	public URI getURI() {
		return getFile().toURI();
	}
	
	/**
	 * @see org.eclipse.core.resources.IStorage#getContents()
	 */
	public InputStream getContents() throws CoreException {
		try {
			return new FileInputStream(getFile());
		} catch (IOException e){
			throw new CoreException(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.OK, "Exception occurred retrieving file contents.", e)); //$NON-NLS-1$
		}
	}

	/**
	 * @see org.eclipse.core.resources.IStorage#getFullPath()
	 */
	public IPath getFullPath() {
		return Path.fromOSString(getFile().getAbsolutePath());
	}

	/**
	 * @see org.eclipse.core.resources.IStorage#getName()
	 */
	public String getName() {
		return getFile().getName();
	}

	/**
	 * @see org.eclipse.core.resources.IStorage#isReadOnly()
	 */
	public boolean isReadOnly() {
		return !getFile().canWrite();
	}

	/**
	 * getFile
	 *
	 * @return File
	 */
	public File getFile() {
		return file;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.resources.UniformResourceStorage#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (IPath.class == adapter) {
			return getFullPath();
		}
		return super.getAdapter(adapter);
	}

}
