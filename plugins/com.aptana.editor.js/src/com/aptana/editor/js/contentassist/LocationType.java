/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

public enum LocationType
{
	// @formatter:off
	UNKNOWN,
	NONE,

	IN_GLOBAL,
	/**
	 * Inside a function invocation where arguments would go (but _not_ on an argument)
	 */
	IN_ARGUMENTS,
	IN_CONSTRUCTOR,
	IN_PROPERTY_NAME,

	/**
	 * Returned when we're in the name of a variable when used as a reference.
	 */
	IN_VARIABLE_NAME,

	/**
	 * Returned when we're on the name of a variable being defined
	 */
	IN_VARIABLE_DECLARATION,
	IN_LABEL,
	IN_OBJECT_LITERAL_PROPERTY,
	IN_THIS;
	// @formatter:on
}