/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * SchemaArray
 */
public class SchemaArray implements IState
{
	private Schema _owningSchema;
	private String _elementTypeName;
	private boolean _inArray;

	/**
	 * SchemaArray
	 * 
	 * @param elementType
	 */
	public SchemaArray(Schema owningSchema, String elementTypeName)
	{
		this._owningSchema = owningSchema;
		this._elementTypeName = elementTypeName;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.State#enter()
	 */
	public void enter()
	{
		this._inArray = false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.State#exit()
	 */
	public void exit()
	{
	}

	/**
	 * getElementType
	 * 
	 * @return
	 */
	public IState getElementType()
	{
		return this._owningSchema.getType(this._elementTypeName);
	}

	/**
	 * getElementTypeName
	 * 
	 * @return
	 */
	public String getElementTypeName()
	{
		return this._elementTypeName;
	}

	/**
	 * getOwningSchema
	 * 
	 * @return
	 */
	public Schema getOwningSchema()
	{
		return this._owningSchema;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IState#isValidTransition(com.aptana.json.EventType, java.lang.Object)
	 */
	public boolean isValidTransition(EventType event, Object value)
	{
		boolean result = false;

		switch (event)
		{
			case START_ARRAY:
				result = this._inArray;
				break;

			case END_ARRAY:
				result = (this._inArray == false);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.State#transition(com.aptana.json.Context, com.aptana.json.EventType, java.lang.Object)
	 */
	public void transition(ISchemaContext context, EventType event, Object value)
	{
		switch (event)
		{
			case START_ARRAY:
				if (this._inArray)
				{
					throw new IllegalStateException("Attempted to start an array that has already been started");
				}

				this._inArray = true;

				// Push element type into current context. Note that processing of that type will automatically remove
				// itself from the stack
				context.pushType(this.getElementType());
				context.saveTop();
				break;

			case END_ARRAY:
				if (this._inArray == false)
				{
					throw new IllegalStateException("Attempted to end an array that has not been started");
				}

				this._inArray = false;

				// Remove this type from the current context
				context.popType();
				break;

			default:
				throw new IllegalStateException("Unsupported event type: " + event.name());
		}
	}
}
