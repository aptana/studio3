/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * SchemaPrimitive
 */
public abstract class SchemaPrimitive implements IState
{
	/**
	 * SchemaPrimitive
	 * 
	 * @param text
	 */
	protected SchemaPrimitive()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.State#enter()
	 */
	public void enter()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.State#exit()
	 */
	public void exit()
	{
	}

	/**
	 * getTypeName
	 * 
	 * @return
	 */
	public abstract String getTypeName();

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IState#isValidTransition(com.aptana.json.EventType, java.lang.Object)
	 */
	public boolean isValidTransition(SchemaEventType event, Object value)
	{
		return event == SchemaEventType.PRIMITIVE && this.validValue(value);
	}

	/**
	 * Determine if the specified value is valid for the given primitive. Subclasses need to override this
	 * 
	 * @param value
	 * @return
	 */
	protected boolean validValue(Object value)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IState#transition(com.aptana.json.ISchemaContext, com.aptana.json.EventType,
	 * java.lang.Object)
	 */
	public void transition(ISchemaContext context, SchemaEventType event, Object value)
	{
		if (!this.isValidTransition(event, value))
		{
			throw new IllegalStateException(); // $codepro.audit.disable exceptionUsage.exceptionCreation
		}

		// TODO: verify popped type matches this type
		context.popType();

		// push type's value onto stack
		context.createType(this.getTypeName(), this, value);
	}
}
