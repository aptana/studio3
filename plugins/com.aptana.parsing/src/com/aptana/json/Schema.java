/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.SourcePrinter;

/**
 * Schema
 */
public class Schema implements IState, IPropertyContainer
{
	private static enum SchemaState
	{
		READY, IN_PARSE, COMPLETE
	}

	private static final IState EMPTY_TYPE = new SchemaObject(null);
	private static final Map<String, IState> BUILTIN_TYPES;

	/**
	 * static initializer
	 */
	static
	{
		BUILTIN_TYPES = new HashMap<String, IState>();
		BUILTIN_TYPES.put("Boolean", new SchemaBoolean()); //$NON-NLS-1$
		BUILTIN_TYPES.put("null", new SchemaNull()); //$NON-NLS-1$
		BUILTIN_TYPES.put("Number", new SchemaNumber()); //$NON-NLS-1$
		BUILTIN_TYPES.put("String", new SchemaString()); //$NON-NLS-1$
	}

	private String _name;
	private String _version;
	private String _description;
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

		if (result == EMPTY_TYPE) // $codepro.audit.disable useEquals
		{
			// type doesn't exist, so create it
			result = this.createObject();

			// and register it
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
	 * @return
	 */
	public SchemaProperty createProperty()
	{
		return new SchemaProperty(this);
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
	 * @return the description
	 */
	public String getDescription()
	{
		return this._description;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return this._name;
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
			// grab built-in type
			result = BUILTIN_TYPES.get(typeName);
		}
		else if (this._typesByName != null)
		{
			// grab registered type
			result = this._typesByName.get(typeName);
		}

		if (result == null)
		{
			if (typeName.startsWith("Array<")) //$NON-NLS-1$
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
	 * @see com.aptana.json.IState#getTypeName()
	 */
	public String getTypeName()
	{
		return "Schema"; //$NON-NLS-1$
	}

	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return this._version;
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
				throw new IllegalArgumentException(Messages.Schema_Expected_End_Of_Array + currentType.getClass().getName());
			}
		}
		else
		{
			throw new IllegalStateException(Messages.Schema_Unsupported_Event + event.name());
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
				throw new IllegalArgumentException(Messages.Schema_Expected_End_Of_Object + currentType.getClass().getName());
			}
		}
		else
		{
			throw new IllegalStateException(Messages.Schema_Unsupported_Event + event.name());
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
			throw new IllegalStateException(Messages.Schema_Unsupported_Event + event.name());
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
				throw new IllegalArgumentException(Messages.Schema_Expected_Primitive + currentType.getClass().getName());
			}
		}
		else
		{
			throw new IllegalStateException(Messages.Schema_Unsupported_Event + event.name());
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
				throw new IllegalArgumentException(Messages.Schema_Expected_Start_Of_Array + currentType.getClass().getName());
			}
		}
		else
		{
			throw new IllegalStateException(Messages.Schema_Unsupported_Event + event.name());
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
				throw new IllegalArgumentException(Messages.Schema_Expected_Start_Of_Object + currentType.getClass().getName());
			}
		}
		else
		{
			throw new IllegalStateException(Messages.Schema_Unsupported_Event + event.name());
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
			throw new IllegalStateException(Messages.Schema_Unsupported_Event + event.name());
		}
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this._description = description;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this._name = name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IPropertyContainer#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String propertyName, String propertyTypeName, Object value)
	{
		if ("name".equals(propertyName)) //$NON-NLS-1$
		{
			this.setName((String) value);
		}
		else if ("version".equals(propertyName)) //$NON-NLS-1$
		{
			this.setVersion((String) value);
		}
		else if ("description".equals(propertyName)) //$NON-NLS-1$
		{
			this.setDescription((String) value);
		}
		else if ("result".equals(propertyName)) //$NON-NLS-1$
		{
			this.setResult((String) value);
		}
		else if ("types".equals(propertyName)) //$NON-NLS-1$
		{
			@SuppressWarnings("unchecked")
			List<Object> types = (List<Object>) value;

			for (Object typeObject : types)
			{
				if (typeObject instanceof SchemaObject)
				{
					SchemaObject object = (SchemaObject) typeObject;
					SchemaProperty nameProperty = object.getProperty("name"); //$NON-NLS-1$

					if (nameProperty != null)
					{
						String name = nameProperty.getValue().toString();

						this.addType(name, object);
					}
					// TODO: else warn?
				}
				// TODO: else warn?
			}
		}
	}

	/**
	 * setRootTypeName
	 * 
	 * @param typeName
	 */
	public void setResult(String typeName)
	{
		this._rootTypeName = typeName;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version)
	{
		this._version = version;
	}

	/**
	 * toSource
	 * 
	 * @return
	 */
	public String toSource()
	{
		SourcePrinter writer = new SourcePrinter();

		this.toSource(writer);

		return writer.toString();
	}

	/**
	 * toSource
	 * 
	 * @param writer
	 */
	public void toSource(SourcePrinter writer)
	{
		writer.printWithIndent("schema ").print('"').print(this.getName()).println('"').increaseIndent(); //$NON-NLS-1$

		writer.printWithIndent("version := ").println(this._version); //$NON-NLS-1$
		writer.printWithIndent("result  := ").println(this._rootTypeName); //$NON-NLS-1$

		List<String> typeNames = new ArrayList<String>(this._typesByName.keySet());
		Collections.sort(typeNames);

		for (String typeName : typeNames)
		{
			writer.printWithIndent("type ").println(typeName).increaseIndent(); //$NON-NLS-1$

			SchemaObject type = (SchemaObject) this._typesByName.get(typeName);
			type.toSource(writer);

			writer.decreaseIndent();
			writer.printlnWithIndent("end type"); //$NON-NLS-1$
		}

		writer.decreaseIndent();
		writer.printlnWithIndent("end schema"); //$NON-NLS-1$
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
					throw new IllegalStateException(Messages.Schema_Unsupported_Event + event.name());
				}
		}
	}
}
