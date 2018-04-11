/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

import java.net.URI;

import org.eclipse.debug.core.model.IDebugTarget;

import com.aptana.js.debug.core.model.IJSScriptElement;

/**
 * @author Max Stepanov
 */
public class JSDebugTopScriptElement extends JSDebugScriptElement {

	private final URI location;

	/**
	 * JSDebugTopScriptElement
	 * 
	 * @param target
	 * @param name
	 * @param location
	 */
	public JSDebugTopScriptElement(IDebugTarget target, String name, URI location) {
		super(target, name, -1, -1);
		this.location = location;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSScriptElement#getLocation()
	 */
	public URI getLocation() {
		return location;
	}

	/*
	 * @see com.aptana.js.debug.core.internal.model.JSDebugScriptElement#setParent(com.aptana.js.debug.core.model.
	 * IJSScriptElement)
	 */
	protected void setParent(IJSScriptElement parent) {
	}
}
