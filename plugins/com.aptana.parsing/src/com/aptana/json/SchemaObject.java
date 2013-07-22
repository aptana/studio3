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

import com.aptana.core.util.SourcePrinter;

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
		return _currentPropertyTypeName;
	}

	/**
	 * hasProperty
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasProperty(String name)
	{
		return this._properties != null && this._properties.containsKey(name);
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
		// emit properties
		if (this._properties != null && !this._properties.isEmpty())
		{
			for (Map.Entry<String, SchemaProperty> entry : this._properties.entrySet())
			{
				SchemaProperty property = entry.getValue();

				property.toSource(writer);
			}
		}
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
					throw new IllegalStateException(Messages.SchemaObject_Cannot_Start_Started_Object);
				}

				// update internal state
				this._currentState = ObjectState.IN_OBJECT;
				break;

			case START_OBJECT_ENTRY:
				if (this._currentState != ObjectState.IN_OBJECT)
				{
					throw new IllegalStateException(Messages.SchemaObject_Cannot_Start_Started_Object_Entry);
				}
				if (value == null || value.toString().length() == 0)
				{
					throw new IllegalStateException(Messages.SchemaObject_Property_Must_Have_Name);
				}

				String name = value.toString();
				SchemaProperty property = this.getProperty(name);

				// update internal state
				this._currentState = ObjectState.IN_PROPERTY;
				this._currentPropertyName = name;

				if (property == null)
				{
					// FIXME Unknown property. We need to ignore and move on, but how do we know what state to
					// transition to? It could be an array, object or primitive!
					this._currentPropertyType = new Schema();
					this._currentPropertyTypeName = _owningSchema.getRootTypeName();
				}
				else
				{
					this._currentPropertyType = property.getType();
					this._currentPropertyTypeName = property.getTypeName();
				}

				// activate this type
				context.pushType(this._currentPropertyName, this._currentPropertyType);

				// fire element type creation event
				if (!(this._currentPropertyType instanceof SchemaPrimitive))
				{
					context.createType(this._currentPropertyTypeName, this._currentPropertyType, value);
				}
				break;

			case END_OBJECT:
				if (this._currentState != ObjectState.IN_OBJECT)
				{
					throw new IllegalStateException(Messages.SchemaObject_Cannot_End_Unstarted_Object);
				}

				// update internal state
				this._currentState = ObjectState.COMPLETE;

				// de-activate this type
				context.popType();
				break;

			case END_OBJECT_ENTRY:
				if (this._currentState != ObjectState.IN_PROPERTY)
				{
					throw new IllegalStateException(Messages.SchemaObject_Cannot_End_Unstarted_Object_Entry);
				}

				// fire property set event
				context.setProperty(this._currentPropertyName, this._currentPropertyTypeName, this._currentPropertyType);

				// update internal state
				this._currentState = ObjectState.IN_OBJECT;
				this._currentPropertyName = null;
				this._currentPropertyType = null;
				break;

			default:
				throw new IllegalStateException(Messages.SchemaObject_Unsupported_Event + event.name());
		}
	}
}
