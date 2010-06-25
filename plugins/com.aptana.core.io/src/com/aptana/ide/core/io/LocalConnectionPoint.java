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
	    if (adapter == File.class) {
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

    @Override
    public boolean load15Data(String data) {
        String[] items = data.split(IConnectionPoint15Constants.DELIMITER);

        if (items.length < 3) {
            return false;
        }

        if (items[0] == null || items[0].equals("")) { //$NON-NLS-1$
            return false;
        }
        setName(items[0]);
        if (items[1] == null || items[1].equals("")) { //$NON-NLS-1$
            setPath(Path.ROOT);
        } else {
            Path path = new Path(items[1]);
            if (path.toFile().exists()) {
                setPath(path);
            } else {
                return false;
            }
        }
        setId(items[2]);

        return true;
    }
}
