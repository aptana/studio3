/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * SchemaState
 */
public interface IState
{
	/**
	 * Enter this new state and perform any processing relevant to this state
	 */
	void enter();

	/**
	 * Exit this state before entering a new state
	 */
	void exit();

	/**
	 * Determine if the current state can transition to the new state.
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	boolean isValidTransition(SchemaEventType event, Object value);

	/**
	 * Transition to a new state from this current state
	 * 
	 * @param context
	 * @param event
	 * @param value
	 */
	void transition(ISchemaContext context, SchemaEventType event, Object value);
}
