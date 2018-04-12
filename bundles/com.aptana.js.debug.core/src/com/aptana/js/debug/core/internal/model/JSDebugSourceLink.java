/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

import java.net.URI;

import com.aptana.js.debug.core.model.ISourceLink;

/**
 * @author Max Stepanov
 */
public class JSDebugSourceLink extends JSDebugElement implements ISourceLink {

	private URI location;

	/**
	 * JSDebugSourceLink
	 * 
	 * @param location
	 */
	public JSDebugSourceLink(URI location) {
		super(null);
		this.location = location;
	}

	/*
	 * @see com.aptana.js.debug.core.model.ISourceLink#getLocation()
	 */
	public URI getLocation() {
		return location;
	}

}
