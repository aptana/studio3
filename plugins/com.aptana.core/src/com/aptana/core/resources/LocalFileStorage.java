/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
