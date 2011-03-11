/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.webserver.core;

import java.net.URI;
import java.net.URL;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;

import com.aptana.core.IURIMapper;
import com.aptana.core.Identifiable;
import com.aptana.core.epl.IMemento;

/**
 * @author Max Stepanov
 * 
 */
public abstract class AbstractWebServerConfiguration implements IExecutableExtension, Identifiable, IURIMapper {

	protected static final String ELEMENT_NAME = "name"; //$NON-NLS-1$

	private String type;
	private String name;

	/**
	 * 
	 */
	protected AbstractWebServerConfiguration() {
	}

	/* (non-Javadoc)
	 * @see com.aptana.webserver.core.IURLMapper#resolve(org.eclipse.core.filesystem.IFileStore)
	 */
	public abstract URI resolve(IFileStore file);

	/* (non-Javadoc)
	 * @see com.aptana.webserver.core.IURLMapper#resolve(java.net.URL)
	 */
	public abstract IFileStore resolve(URI uri);

	public abstract URL getBaseURL();
	
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
	 * Returns true if this type of configurations should be persistent by manager
	 * @return
	 */
	protected boolean isPersistent() {
		return true;
	}

	/*
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public final void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		type = config.getAttribute(ServerConfigurationManager.ATT_ID);
	}

	/* package */final String getType() {
		return type;
	}

	/*
	 * @see com.aptana.core.Identifiable#getId()
	 */
	public final String getId() {
		return type;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}
}
