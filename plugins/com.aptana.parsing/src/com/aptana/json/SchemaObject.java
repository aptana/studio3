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
 * SchemaObject
 */
public class SchemaObject implements IState, IPropertyContainer
{
	private enum ObjectState
	{
		READY, IN_OBJECT, IN_PROPERTY, COMPLETE
	};

	private Schema _owningSchema;
	private Map<String, SchemaProperty> _properties;
	private String _description;

	private ObjectState _currentState;
	private String _currentPropertyName;
	private IState _currentPropertyType;
	private String _currentPropertyTypeName;

	/**
	 * SchemaObject
	 * 
	 * @param owningSchema
	 */
	public SchemaObject(Schema owningSchema)
	{
		this._owningSchema = owningSchema;
	}

	/**
	 * addProperty
	 * 
	 * @param property
	 */
	public void addProperty(SchemaProperty property)
	{
		if (this._properties == null)
		{
			this._properties = new HashMap<String, SchemaProperty>();
		}

		this._properties.put(property.getName(), property);
	}

	/**
	 * addProperty
	 * 
	 * @param name
	 * @param typeName
	 */
	public SchemaProperty addProperty(String name, String typeName)
	{
		SchemaProperty result = this._owningSchema.createProperty(name, typeName);

		this.addProperty(result);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.State#enter()
	 */
	public void enter()
	{
		this._currentState = ObjectState.READY;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.State#exit()
	 */
	public void exit()
	{
	}

	/**
	 * getDescription
	 * 
	 * @return the description
	 */
	public String getDescription()
	{
		return this._description;
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

	/**
	 * getProperty
	 * 
	 * @param name
	 * @return
	 */
	public SchemaProperty getProperty(String name)
	{
		SchemaProperty result = null;

		if (this._properties != null)
		{
			result = this._properties.get(name);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IState#getTypeName()
	 */
	public String getTypeName()
	{
		return this.getTypeName();
	}

	/**
	 * hasProperty
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasProperty(String name)
	{
		return (this._properties != null && this._properties.containsKey(name));
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
			case START_OBJECT:
				result = (this._currentState == ObjectState.READY);
				break;

			case START_OBJECT_ENTRY:
				result = (this._currentState == ObjectState.IN_OBJECT && value != null && value.toString().length() > 0);

				// TODO: check property name?
				break;

			case END_OBJECT:
				result = (this._currentState == ObjectState.IN_OBJECT);
				break;

			case END_OBJECT_ENTRY:
				result = (this._currentState == ObjectState.IN_PROPERTY);
				break;
		}

		return result;
	}

	/**
	 * setDescription
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this._description = description;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IPropertyContainer#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String propertyName, String propertyTypeName, Object value)
	{
		SchemaProperty property = this.addProperty(propertyName, propertyTypeName);

		property.setValue(value);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();

		// open
		buffer.append("{");

		// emit properties
		if (this._properties != null && this._properties.isEmpty() == false)
		{
			for (Map.Entry<String, SchemaProperty> entry : this._properties.entrySet())
			{
				SchemaProperty property = entry.getValue();

				buffer.append("\n\t").append(property);
			}

			buffer.append("\n");
		}

		// close
		buffer.append("}");

		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.State#transition(com.aptana.json.Context, com.aptana.json.EventType, java.lang.Object)
	 */
	public void transition(ISchemaContext context, SchemaEventType event, Object value)
	{
		switch (event)
		{
			case START_OBJECT:
				if (this._currentState != ObjectState.READY)
				{
					throw new IllegalStateException("Attempted to start and object that has already been started");
				}

				// update internal state
				this._currentState = ObjectState.IN_OBJECT;
				break;

			case START_OBJECT_ENTRY:
				if (this._currentState != ObjectState.IN_OBJECT)
				{
					throw new IllegalStateException("Attempted to start an object entry in an object that has not been started");
				}
				if (value == null || value.toString().length() == 0)
				{
					throw new IllegalStateException("Attempted to start an object entry without providing its name");
				}

				String name = value.toString();
				SchemaProperty property = this.getProperty(name);

				if (property == null)
				{
					throw new IllegalStateException("Attempted to start an object entry that does not exist in this object: " + name);
				}

				// update internal state
				this._currentState = ObjectState.IN_PROPERTY;
				this._currentPropertyName = name;
				this._currentPropertyType = property.getType();
				this._currentPropertyTypeName = property.getTypeName();

				// activate this type
				context.pushType(this._currentPropertyName, this._currentPropertyType);

				// fire element type creation event
				if (this._currentPropertyType instanceof SchemaPrimitive == false)
				{
					context.createType(this._currentPropertyTypeName, this._currentPropertyType, value);
				}
				break;

			case END_OBJECT:
				if (this._currentState != ObjectState.IN_OBJECT)
				{
					throw new IllegalStateException("Attempted to end an object that has an open object entry");
				}

				// update internal state
				this._currentState = ObjectState.COMPLETE;

				// de-activate this type
				context.popType();
				break;

			case END_OBJECT_ENTRY:
				if (this._currentState != ObjectState.IN_PROPERTY)
				{
					throw new IllegalStateException("Attempted to end an object entry in an object that has not been started");
				}

				// fire property set event
				context.setProperty(this._currentPropertyName, this._currentPropertyTypeName, this._currentPropertyType);

				// update internal state
				this._currentState = ObjectState.IN_OBJECT;
				this._currentPropertyName = null;
				this._currentPropertyType = null;
				break;

			default:
				throw new IllegalStateException("Unsupported event type: " + event.name());
		}
	}
}
