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
import java.util.UUID;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;

import com.aptana.core.epl.IMemento;
import com.aptana.ide.core.io.vfs.VirtualConnectionManager;

/**
 * Base class for all connection points
 * 
 * @author Max Stepanov
 *
 */
public abstract class ConnectionPoint extends PlatformObject implements IConnectionPoint, IExecutableExtension {

	protected static final String ELEMENT_NAME = "name"; //$NON-NLS-1$

	private String id;
	private String type;
	private boolean dirty;
	
	protected String name;
	
	/**
	 * 
	 */
	protected ConnectionPoint(String type) {
		this.type = type;
		setId(UUID.randomUUID().toString());
	}

	/**
	 * 
	 */
	protected ConnectionPoint() {
		this(""); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public final void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		setType(config.getAttribute(ConnectionPointManager.ATT_ID));
	}
	
	protected boolean isPersistent() {
		return true;
	}

	protected void loadState(IMemento memento) {
		IMemento child = memento.getChild(ELEMENT_NAME);
		if (child != null) {
			name = child.getTextData();
		}		
	}
	
	protected void saveState(IMemento memento) {
		memento.createChild(ELEMENT_NAME).putTextData(name);
	}

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	protected final void setId(String id) {
		this.id = id;
		VirtualConnectionManager.getInstance().register(this);
	}

	/**
	 * @return the type
	 */
	public final String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	protected final void setType(String type) {
		this.type = type;
	}	
	
	protected final void notifyChanged() {
		dirty = true;
	}
	
	/* package */ final boolean isChanged() {
		try {
			return dirty;
		} finally {
			dirty = false;
		}		
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
		notifyChanged();
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#getRootURI()
	 */
	public URI getRootURI() {
		return VirtualConnectionManager.getInstance().getConnectionVirtualURI(this);
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#getRoot()
	 */
	public IFileStore getRoot() throws CoreException {
		return EFS.getStore(getRootURI());
	}

    /* (non-Javadoc)
     * @see com.aptana.ide.core.io.IConnectionPoint#connect(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void connect(IProgressMonitor monitor) throws CoreException {
        connect(false, monitor);
    }

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#connect(boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void connect(boolean force, IProgressMonitor monitor) throws CoreException {
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#isConnected()
	 */
	public boolean isConnected() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#canDisconnect()
	 */
	public boolean canDisconnect() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#disconnect(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void disconnect(IProgressMonitor monitor) throws CoreException {
	}

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
	@Override
    public Object getAdapter(Class adapter) {
        if (IFileStore.class == adapter) {
            try {
                return getRoot();
            } catch (CoreException e) {
                return null;
            }
        }
        return super.getAdapter(adapter);
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Loads the connection data that has 1.5 format.
     * 
     * @param data
     *            the connection data
     * @return true if loading is successful, false otherwise
     */
    public boolean load15Data(String data) {
        return false;
    }
}
