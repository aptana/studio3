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

import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.epl.IMemento;
import com.aptana.ide.core.io.efs.WorkspaceFileSystem;


/**
 * @author Max Stepanov
 *
 */
public final class WorkspaceConnectionPoint extends ConnectionPoint {

	public static final String TYPE = "workspace"; //$NON-NLS-1$
    
	private static final String ELEMENT_PATH = "path"; //$NON-NLS-1$

	private static IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

	private IPath path; /* workspace-relative path */
	
	/**
	 * Default constructor
	 */
	public WorkspaceConnectionPoint() {
		super(TYPE);
	}

	/**
	 * 
	 */
	public WorkspaceConnectionPoint(IContainer resource) {
		super(TYPE);
		this.path = resource.getFullPath();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (IResource.class.isAssignableFrom(adapter)) {
			IContainer resource = getResource();
			if (resource != null) {
				Object result = resource.getAdapter(adapter);
				if (result != null) {
					return result;
				}
			}
		}
		return super.getAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#getRootURI()
	 */
	@Override
    public URI getRootURI() {
		return WorkspaceFileSystem.getInstance().getStore(path).toURI();
    }

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#getRoot()
	 */
	@Override
	public IFileStore getRoot() throws CoreException {
		return WorkspaceFileSystem.getInstance().getStore(path);
	}

	/**
	 * @return the resource
	 */
	public IContainer getResource() {
		IResource resource = workspaceRoot.findMember(path);
		if (resource instanceof IContainer) {
			return (IContainer) resource;
		}
		return null;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(IContainer resource) {
		this.path = resource.getFullPath();
	}
	
	public IPath getPath() {
		return path;
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
            return false;
        } else {
            IResource resource = workspaceRoot.findMember(items[1]);
            if (resource instanceof IContainer) {
                setResource((IContainer) resource);
            } else {
                return false;
            }
        }
        setId(items[2]);

        return true;
    }
}
