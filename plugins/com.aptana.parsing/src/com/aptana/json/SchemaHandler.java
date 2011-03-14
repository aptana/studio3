/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * SchemaHandler
 */
public class SchemaHandler implements IContextHandler
{
	private static enum PropertyName
	{
		UNDEFINED(""), //$NON-NLS-1$
		NAME("name"), //$NON-NLS-1$
		VERSION("version"), //$NON-NLS-1$
		DESCRIPTION("description"), //$NON-NLS-1$
		RESULT("result"), //$NON-NLS-1$
		TYPES("types"), //$NON-NLS-1$
		PROPERTIES("properties"), //$NON-NLS-1$
		TYPE("type"), //$NON-NLS-1$
		EXAMPLE("example"), //$NON-NLS-1$
		OPTIONAL("optional"); //$NON-NLS-1$

		private static Map<String, PropertyName> NAME_MAP;
		private String _name;

		static
		{
			NAME_MAP = new HashMap<String, PropertyName>();

			for (PropertyName property : EnumSet.allOf(PropertyName.class))
			{
				NAME_MAP.put(property.getName(), property);
			}
		}

		public static PropertyName get(String name)
		{
			PropertyName result = UNDEFINED;

			if (NAME_MAP.containsKey(name))
			{
				result = NAME_MAP.get(name);
			}

			return result;
		}

		private PropertyName(String name)
		{
			this._name = name;
		}

		public String getName()
		{
			return this._name;
		}
	}

	private static enum TypeName
	{
		UNDEFINED(""), //$NON-NLS-1$
		SCHEMA("Schema"), //$NON-NLS-1$
		TYPE("Type"), //$NON-NLS-1$
		PROPERTY("Property"), //$NON-NLS-1$
		STRING("String"), //$NON-NLS-1$
		BOOLEAN("Boolean"); //$NON-NLS-1$

		private static Map<String, TypeName> NAME_MAP;
		private String _name;

		static
		{
			NAME_MAP = new HashMap<String, TypeName>();

			for (TypeName type : EnumSet.allOf(TypeName.class))
			{
				NAME_MAP.put(type.getName(), type);
			}
		}

		public static TypeName get(String name)
		{
			TypeName result = UNDEFINED;

			if (NAME_MAP.containsKey(name))
			{
				result = NAME_MAP.get(name);
			}

			return result;
		}

		private TypeName(String name)
		{
			this._name = name;
		}

		public String getName()
		{
			return this._name;
		}
	}

	private Schema _currentSchema;
	private SchemaObject _currentType;
	private String _currentTypeName;
	private SchemaProperty _currentProperty;
	private String _currentString;
	private Boolean _currentBoolean;

	/**
	 * SchemaHandler
	 */
	public SchemaHandler()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextAction#addElement(java.lang.String, com.aptana.json.IState)
	 */
	public void addElement(String elementTypeName, IState elementType)
	{
		TypeName t = TypeName.get(elementTypeName);

		switch (t)
		{
			case SCHEMA:
				// n/a
				break;

			case TYPE:
				this._currentSchema.addType(this._currentTypeName, this._currentType);
				this._currentTypeName = null;
				this._currentType = null;
				break;

			case PROPERTY:
				this._currentType.addProperty(this._currentProperty);
				this._currentProperty = null;
				break;

			default:
				// warn
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextHandler#createType(java.lang.String, com.aptana.json.IState, java.lang.Object)
	 */
	public void createType(String typeName, IState type, Object value)
	{
		TypeName t = TypeName.get(typeName);

		switch (t)
		{
			case SCHEMA:
				this._currentSchema = new Schema();
				break;

			case TYPE:
				this._currentType = this._currentSchema.createObject();
				break;

			case PROPERTY:
				this._currentProperty = this._currentSchema.createProperty();
				break;

			case STRING:
				this._currentString = (String) value;
				break;

			case BOOLEAN:
				this._currentBoolean = (Boolean) value;
				break;

			default:
				// warn
		}
	}

	/**
	 * getSchema
	 * 
	 * @return
	 */
	public Schema getSchema()
	{
		return this._currentSchema;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextAction#setProperty(java.lang.String, com.aptana.json.IState)
	 */
	public void setProperty(String propertyName, String propertyTypeName, IState propertyType)
	{
		PropertyName p = PropertyName.get(propertyName);

		switch (p)
		{
			case NAME:
				if (this._currentProperty != null)
				{
					this._currentProperty.setName(this._currentString);
				}
				else if (this._currentType != null)
				{
					this._currentTypeName = this._currentString;
				}
				else if (this._currentSchema != null)
				{
					this._currentSchema.setName(this._currentString);
				}
				this._currentString = null;
				break;

			case VERSION:
				if (this._currentSchema != null)
				{
					this._currentSchema.setVersion(this._currentString);
				}
				this._currentString = null;
				break;

			case DESCRIPTION:
				if (this._currentProperty != null)
				{
					this._currentProperty.setDescription(this._currentString);
				}
				else if (this._currentType != null)
				{
					this._currentType.setDescription(this._currentString);
				}
				else if (this._currentSchema != null)
				{
					this._currentSchema.setDescription(this._currentString);
				}
				this._currentString = null;
				break;

			case RESULT:
				if (this._currentSchema != null)
				{
					this._currentSchema.setResult(this._currentString);
				}
				this._currentString = null;
				break;

			case TYPE:
				if (this._currentProperty != null)
				{
					this._currentProperty.setTypeName(this._currentString);
				}
				this._currentString = null;
				break;

			case EXAMPLE:
				if (this._currentProperty != null)
				{
					this._currentProperty.setExample(this._currentString);
				}
				this._currentString = null;
				break;

			case OPTIONAL:
				if (this._currentProperty != null)
				{
					this._currentProperty.setOptional(this._currentBoolean);
				}
				this._currentBoolean = null;
				break;

			case TYPES:
			case PROPERTIES:
				// implicitly handled in addElement
				break;

			default:
				// warn
		}
	}
}
