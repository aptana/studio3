/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.lexer;

public interface ITypePredicate
{
	/**
	 * Return the short index of a given enumeration value
	 * 
	 * @return
	 */
	short getIndex();

	/**
	 * Return true if this is not an UNDEFINED enumeration value
	 * 
	 * @return
	 */
	boolean isDefined();
}
