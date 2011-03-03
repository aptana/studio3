/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * ISchemaContext
 */
public interface ISchemaContext extends IContextHandler
{
	/**
	 * Return the type that is currently active in this context
	 * 
	 * @return
	 */
	IState getCurrentType();

	/**
	 * Replace the currently active type with whatever type was pushed before it
	 */
	void popType();

	/**
	 * Replace the currently active type with the specified type after pushing the current type onto a stack for later
	 * retrieval
	 * 
	 * @param typeName
	 * @param type
	 */
	void pushType(String typeName, IState type);

	/**
	 * Reset the context. This clears all state.
	 */
	void reset();
}