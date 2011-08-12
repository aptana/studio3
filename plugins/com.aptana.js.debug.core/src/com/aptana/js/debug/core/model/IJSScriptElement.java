/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.model;

import java.net.URI;

import org.eclipse.debug.core.model.IDebugElement;

/**
 * @author Max Stepanov
 */
public interface IJSScriptElement extends IDebugElement {

	/**
	 * Function name
	 * 
	 * @return String
	 */
	String getName();

	/**
	 * Function location (path or URL)
	 * 
	 * @return String
	 */
	URI getLocation();

	/**
	 * Parent function
	 * 
	 * @return IJSScriptElement
	 */
	IJSScriptElement getParent();

	/**
	 * Nested functions
	 * 
	 * @return IJSScriptElement[]
	 */
	IJSScriptElement[] getChildren();

	/**
	 * The first line of the function in source code
	 * 
	 * @return int
	 */
	int getBaseLine();

	/**
	 * Length of the function in lines
	 * 
	 * @return int
	 */
	int getLineExtent();
}
