/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import com.aptana.core.util.SourcePrinter;

/**
 * SchemaProperty
 */
public class SchemaProperty
{
	private Schema _owningSchema;
	private String _name;
	private String _typeName;
	private String _description;
	private String _example;
	private boolean _optional;
	private Object _value;

	/**
	 * SchemaProperty
	 * 
	 * @param owningSchema
	 */
	SchemaProperty(Schema owningSchema)
	{
		this(owningSchema, null, null);
	}

	/**
	 * SchemaProperty
	 * 
	 * @param name
	 * @param typeName
	 */
	SchemaProperty(Schema owningSchema, String name, String typeName)
	{
		this._owningSchema = owningSchema;
		this._name = name;
		this._typeName = typeName;
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
	 * @return the example
	 */
	public String getExample()
	{
		return this._example;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
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
	 * getType
	 * 
	 * @return
	 */
	public IState getType()
	{
		return this._owningSchema.getType(this._typeName);
	}

	/**
	 * getTypeName
	 * 
	 * @return
	 */
	public String getTypeName()
	{
		return this._typeName;
	}

	/**
	 * @return the value
	 */
	public Object getValue()
	{
		return this._value;
	}

	/**
	 * isOptional
	 * 
	 * @return the optional
	 */
	public boolean isOptional()
	{
		return this._optional;
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

	/**
	 * @param example
	 *            the example to set
	 */
	public void setExample(String example)
	{
		this._example = example;
	}

	/**
	 * setName
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this._name = name;
	}

	/**
	 * setOptional
	 * 
	 * @param optional
	 *            the optional to set
	 */
	public void setOptional(boolean optional)
	{
		this._optional = optional;
	}

	/**
	 * setTypeName
	 * 
	 * @param typeName
	 */
	public void setTypeName(String typeName)
	{
		this._typeName = typeName;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Object value)
	{
		this._value = value;
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
		writer.printWithIndent(this.getName()).print(" : ").print(this.getTypeName()); //$NON-NLS-1$

		if (this._value != null)
		{
			writer.print(" := ").print(this._value); //$NON-NLS-1$
		}

		writer.println();
	}
}
