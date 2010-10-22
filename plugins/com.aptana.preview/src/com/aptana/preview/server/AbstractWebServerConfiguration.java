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

package com.aptana.preview.server;

import java.net.URL;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;

import com.aptana.core.Identifiable;
import com.aptana.core.epl.IMemento;

/**
 * @author Max Stepanov
 * 
 */
public abstract class AbstractWebServerConfiguration implements IExecutableExtension, Identifiable {

	protected static final String ELEMENT_NAME = "name"; //$NON-NLS-1$

	private String type;
	private String name;

	/**
	 * 
	 */
	protected AbstractWebServerConfiguration() {
	}

	public abstract URL resolve(IFileStore file);

	public abstract IFileStore resolve(URL url);

	protected void loadState(IMemento memento) {
		IMemento child = memento.getChild(ELEMENT_NAME);
		if (child != null) {
			name = child.getTextData();
		}
	}

	protected void saveState(IMemento memento) {
		memento.createChild(ELEMENT_NAME).putTextData(name);
	}

	protected boolean isPersistent() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org
	 * .eclipse.core.runtime.IConfigurationElement, java.lang.String,
	 * java.lang.Object)
	 */
	public final void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		type = config.getAttribute(ServerConfigurationManager.ATT_ID);
	}

	/* package */final String getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
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
