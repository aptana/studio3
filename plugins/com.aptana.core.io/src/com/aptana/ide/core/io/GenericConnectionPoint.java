/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io;

import java.net.URI;

import org.eclipse.core.runtime.Assert;

import com.aptana.core.epl.IMemento;

/**
 * @author Max Stepanov
 *
 */
public class GenericConnectionPoint extends ConnectionPoint {

	public static final String TYPE = "generic"; //$NON-NLS-1$

	private static final String ELEMENT_URI = "uri"; //$NON-NLS-1$

	private URI uri;
	
	public GenericConnectionPoint() {
		super(TYPE);
	}

	/**
	 * @return the URI
	 */
	public URI getURI() {
		return uri;
	}

	/**
	 * @param uri the URI to set
	 */
	public void setURI(URI uri) {
		this.uri = uri;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#getRootURI()
	 */
	@Override
	public URI getRootURI() {
		return getURI();
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#loadState(com.aptana.ide.core.epl.IMemento)
	 */
	@Override
	protected void loadState(IMemento memento) {
		super.loadState(memento);
		IMemento child = memento.getChild(ELEMENT_URI);
		if (child != null) {
			uri = URI.create(child.getTextData());
			Assert.isTrue(uri.isAbsolute());
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#saveState(com.aptana.ide.core.epl.IMemento)
	 */
	@Override
	protected void saveState(IMemento memento) {
		super.saveState(memento);
		memento.createChild(ELEMENT_URI).putTextData(uri.toString());
	}
}
