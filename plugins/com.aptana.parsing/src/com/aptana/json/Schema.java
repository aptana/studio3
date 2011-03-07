/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import java.util.HashMap;
import java.util.Map;

/**
 * Schema
 */
public class Schema implements IState
{
	private static enum SchemaState
	{
		READY, IN_PARSE, COMPLETE
	}

	private static final IState EMPTY_TYPE = new SchemaObject(null);
	private static final Map<String, IState> BUILTIN_TYPES;

	static
	{
		BUILTIN_TYPES = new HashMap<String, IState>();
		BUILTIN_TYPES.put("Boolean", new SchemaBoolean());
		BUILTIN_TYPES.put("null", new SchemaNull());
		BUILTIN_TYPES.put("Number", new SchemaNumber());
		BUILTIN_TYPES.put("String", new SchemaString());
	}

	private Map<String, IState> _typesByName;
	private String _rootTypeName;
	private SchemaState _currentState;

	/**
	 * createType
	 * 
	 * @param typeName
	 * @return
	 */
	public IState addType(String typeName)
	{
		IState result = this.getType(typeName);

		if (result == EMPTY_TYPE)
		{
			result = this.createObject();
			this.addType(typeName, result);
		}

		return result;
	}

	/**
	 * addType
	 * 
	 * @param type
	 */
	protected void addType(String name, IState type)
	{
		if (this._typesByName == null)
		{
			this._typesByName = new HashMap<String, IState>();
		}

		// TODO: Warn on duplicate entry or merge entries?
		this._typesByName.put(name, type);
	}

	/**
	 * createArray
	 * 
	 * @param elementType
	 * @return
	 */
	public SchemaArray createArray(String elementType)
	{
		return new SchemaArray(this, elementType);
	}

	/**
	 * createObject
	 * 
	 * @return
	 */
	public SchemaObject createObject()
	{
		return new SchemaObject(this);
	}

	/**
	 * createProperty
	 * 
	 * @param name
	 * @param typeName
	 * @return
	 */
	public SchemaProperty createProperty(String name, String typeName)
	{
		return new SchemaProperty(this, name, typeName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.SchemaState#enter()
	 */
	public void enter()
	{
		this._currentState = SchemaState.READY;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.SchemaState#exit()
	 */
	public void exit()
	{
		this._currentState = null;
	}

	/**
	 * getRootType
	 * 
	 * @return
	 */
	public IState getRootType()
	{
		return this.getType(this.getRootTypeName());
	}

	/**
	 * getRootTypeName
	 * 
	 * @return
	 */
	public String getRootTypeName()
	{
		return this._rootTypeName;
	}

	/**
	 * getType
	 * 
	 * @param typeName
	 * @return
	 */
	public IState getType(String typeName)
	{
		IState result = null;

		if (BUILTIN_TYPES.containsKey(typeName))
		{
			result = BUILTIN_TYPES.get(typeName);
		}
		else
		{
			if (this._typesByName != null)
			{
				result = this._typesByName.get(typeName);
			}
		}

		if (result == null)
		{
			if (typeName.startsWith("Array<"))
			{
				String elementType = typeName.substring(6, typeName.length() - 1);

				result = this.createArray(elementType);
			}
			else
			{
				result = EMPTY_TYPE;
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IState#isValidTransition(com.aptana.json.EventType, java.lang.Object)
	 */
	public boolean isValidTransition(SchemaEventType event, Object value)
	{
		boolean result = false;

		switch (event)
		{
			case START_PARSE:
				result = (this._currentState == SchemaState.READY);
				break;

			case END_PARSE:
				result = (this._currentState == SchemaState.IN_PARSE);
				break;

			default:
				result = (this._currentState == SchemaState.READY);
		}

		return result;
	}

	/**
	 * processEndArray
	 * 
	 * @param context
	 * @param event
	 * @param value
	 */
	protected void processEndArray(ISchemaContext context, SchemaEventType event, Object value)
	{
		if (this._currentState == SchemaState.IN_PARSE)
		{
			IState currentType = context.getCurrentType();

			if (currentType instanceof SchemaArray)
			{
				if (currentType != null)
				{
					currentType.transition(context, event, value);
					currentType.exit();
				}

				// check for possible END_ARRAY_ENTRY event state
				currentType = context.getCurrentType();

				if (currentType instanceof SchemaArray)
				{
					currentType.transition(context, SchemaEventType.END_ARRAY_ENTRY, value);
				}
			}
			else
			{
				throw new IllegalArgumentException("Tried to end an array on a non-array type: " + currentType.getClass().getName());
			}
		}
		else
		{
			throw new IllegalStateException("Unsupported event type: " + event.name());
		}
	}

	/**
	 * processEndObject
	 * 
	 * @param context
	 * @param event
	 * @param value
	 */
	protected void processEndObject(ISchemaContext context, SchemaEventType event, Object value)
	{
		if (this._currentState == SchemaState.IN_PARSE)
		{
			IState currentType = context.getCurrentType();

			if (currentType instanceof SchemaObject)
			{
				if (currentType != null)
				{
					currentType.transition(context, event, value);
					currentType.exit();
				}

				// check for possible END_ARRAY_ENTRY event state
				currentType = context.getCurrentType();

				if (currentType instanceof SchemaArray)
				{
					currentType.transition(context, SchemaEventType.END_ARRAY_ENTRY, value);
				}
			}
			else
			{
				throw new IllegalArgumentException("Tried to end an object on a non-object type: " + currentType.getClass().getName());
			}
		}
		else
		{
			throw new IllegalStateException("Unsupported event type: " + event.name());
		}
	}

	/**
	 * processEndParse
	 * 
	 * @param context
	 * @param event
	 * @param value
	 */
	protected void processEndParse(ISchemaContext context, SchemaEventType event, Object value)
	{
		if (this._currentState == SchemaState.IN_PARSE)
		{
			this._currentState = SchemaState.COMPLETE;
		}
		else
		{
			throw new IllegalStateException("Unsupported event type: " + event.name());
		}
	}

	/**
	 * processPrimitive
	 * 
	 * @param context
	 * @param event
	 * @param value
	 */
	protected void processPrimitive(ISchemaContext context, SchemaEventType event, Object value)
	{
		if (this._currentState == SchemaState.IN_PARSE)
		{
			IState currentType = context.getCurrentType();

			if (currentType instanceof SchemaArray)
			{
				currentType.transition(context, SchemaEventType.START_ARRAY_ENTRY, value);

				// the current type should have been updated, so grab the new value
				currentType = context.getCurrentType();
			}

			if (currentType instanceof SchemaPrimitive)
			{
				if (currentType != null)
				{
					currentType.enter();
					currentType.transition(context, event, value);
					currentType.exit();
				}

				// check for possible END_ARRAY_ENTRY event state
				currentType = context.getCurrentType();

				if (currentType instanceof SchemaArray)
				{
					currentType.transition(context, SchemaEventType.END_ARRAY_ENTRY, value);
				}
			}
			else
			{
				throw new IllegalArgumentException("Tried to process non-primitive type as a primitive: " + currentType.getClass().getName());
			}
		}
		else
		{
			throw new IllegalStateException("Unsupported event type: " + event.name());
		}
	}

	/**
	 * processStartArray
	 * 
	 * @param context
	 * @param event
	 * @param value
	 */
	protected void processStartArray(ISchemaContext context, SchemaEventType event, Object value)
	{
		if (this._currentState == SchemaState.IN_PARSE)
		{
			IState currentType = context.getCurrentType();

			if (currentType instanceof SchemaArray && currentType.isValidTransition(SchemaEventType.START_ARRAY_ENTRY, value))
			{
				currentType.transition(context, SchemaEventType.START_ARRAY_ENTRY, value);

				// the current type should have been updated, so grab the new value
				currentType = context.getCurrentType();
			}

			if (currentType instanceof SchemaArray)
			{
				if (currentType != null)
				{
					currentType.enter();
					currentType.transition(context, event, value);
				}
			}
			else
			{
				throw new IllegalArgumentException("Tried to start an array on a non-array type: " + currentType.getClass().getName());
			}
		}
		else
		{
			throw new IllegalStateException("Unsupported event type: " + event.name());
		}
	}

	/**
	 * processStartObject
	 * 
	 * @param context
	 * @param event
	 * @param value
	 */
	protected void processStartObject(ISchemaContext context, SchemaEventType event, Object value)
	{
		if (this._currentState == SchemaState.IN_PARSE)
		{
			IState currentType = context.getCurrentType();

			if (currentType instanceof SchemaArray)
			{
				currentType.transition(context, SchemaEventType.START_ARRAY_ENTRY, value);

				// the current type should have been updated, so grab the new value
				currentType = context.getCurrentType();
			}

			if (currentType instanceof SchemaObject)
			{
				if (currentType != null)
				{
					currentType.enter();
					currentType.transition(context, event, value);
				}
			}
			else
			{
				throw new IllegalArgumentException("Tried to start an object on a non-object type: " + currentType.getClass().getName());
			}
		}
		else
		{
			throw new IllegalStateException("Unsupported event type: " + event.name());
		}
	}

	/**
	 * processStartParse
	 * 
	 * @param context
	 * @param event
	 * @param value
	 */
	protected void processStartParse(ISchemaContext context, SchemaEventType event, Object value)
	{
		if (this._currentState == SchemaState.READY)
		{
			// update internal state
			this._currentState = SchemaState.IN_PARSE;

			// push type onto context stack
			context.pushType(this.getRootTypeName(), this.getRootType());

			// fire type creation event
			context.createType(this.getRootTypeName(), this.getRootType(), value);
		}
		else
		{
			throw new IllegalStateException("Unsupported event type: " + event.name());
		}
	}

	/**
	 * setRootTypeName
	 * 
	 * @param typeName
	 */
	public void setRootTypeName(String typeName)
	{
		this._rootTypeName = typeName;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.SchemaState#transition(com.aptana.json.EventType)
	 */
	public void transition(ISchemaContext context, SchemaEventType event, Object value)
	{
		switch (event)
		{
			case START_PARSE:
				this.processStartParse(context, event, value);
				break;

			case START_OBJECT:
				this.processStartObject(context, event, value);
				break;

			case START_ARRAY:
				this.processStartArray(context, event, value);
				break;

			case PRIMITIVE:
				this.processPrimitive(context, event, value);
				break;

			case END_OBJECT:
				this.processEndObject(context, event, value);
				break;

			case END_ARRAY:
				this.processEndArray(context, event, value);
				break;

			case END_PARSE:
				this.processEndParse(context, event, value);
				break;

			default:
				if (this._currentState == SchemaState.IN_PARSE)
				{
					IState currentType = context.getCurrentType();

					if (currentType != null)
					{
						// delegate to model
						currentType.transition(context, event, value);
					}
				}
				else
				{
					throw new IllegalStateException("Unsupported event type: " + event.name());
				}
		}
	}
}
