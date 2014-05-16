/**
 * Aptana Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.model;

import java.util.List;

/**
 * A marking interface to know that this model element has a set of possible predefined values that can be used.
 * 
 * @author Chris Williams <cwilliams@appcelerator.com>
 */
public interface IHasPredefinedValues
{
	/**
	 * Property name used to store the list/array of String values in JSCA
	 */
	static final String CONSTANTS_PROPERTY = "constants"; //$NON-NLS-1$

	public List<String> getConstants();
}
