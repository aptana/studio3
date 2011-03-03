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
public interface ISchemaContext
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
	 * @param type
	 */
	void pushType(IState type);

	/**
	 * Reset the context. This clears all state.
	 */
	void reset();

	/**
	 * Restore the stack top to the previously saved value
	 */
	void restoreTop();

	/**
	 * Set the current stack position as the bottom of the stack. This will prevent all items the currently exist on the
	 * stack from being popped.
	 */
	void saveTop();
}