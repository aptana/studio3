/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions

package com.aptana.ide.core.io;

import java.io.File;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.epl.IMemento;

/**
 * @author Max Stepanov
 *
 */
public class LocalConnectionPoint extends ConnectionPoint {

	public static final String TYPE = "local"; //$NON-NLS-1$
	
	private static final String ELEMENT_PATH = "path"; //$NON-NLS-1$

	private IPath path; /* absolute local path */
	
	/**
	 * Default constructor
	 */
	public LocalConnectionPoint() {
		super(TYPE);
	}

	/**
	 * 
	 * @param path
	 */
	public LocalConnectionPoint(IPath path) {
		super(TYPE);
		this.path = path;
	}

	/**
	 * @return the path
	 */
	public IPath getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(IPath path) {
		this.path = path;
	}
	
	/**
	 * 
	 * @return file
	 */
	public File getFile() {
		return path.toFile();
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#getRootURI()
	 */
	@Override
	public URI getRootURI() {
		return (EFS.getLocalFileSystem().fromLocalFile(path.toFile())).toURI();
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#getRoot()
	 */
	@Override
	public IFileStore getRoot() throws CoreException {
		return EFS.getLocalFileSystem().fromLocalFile(path.toFile());
	}

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
     */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
	    if (File.class.equals(adapter)) {
	        return getFile();
	    }
	    return super.getAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#loadState(com.aptana.ide.core.epl.IMemento)
	 */
	@Override
	protected void loadState(IMemento memento) {
		super.loadState(memento);
		IMemento child = memento.getChild(ELEMENT_PATH);
		if (child != null) {
			path = Path.fromPortableString(child.getTextData());
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#saveState(com.aptana.ide.core.epl.IMemento)
	 */
	@Override
	protected void saveState(IMemento memento) {
		super.saveState(memento);
		memento.createChild(ELEMENT_PATH).putTextData(path.toPortableString());
	}
}
