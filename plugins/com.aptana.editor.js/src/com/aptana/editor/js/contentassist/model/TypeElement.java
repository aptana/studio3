/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mortbay.util.ajax.JSON.Output;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexUtil;

public class TypeElement extends BaseElement
{
	private static final String FUNCTIONS_PROPERTY = "functions"; //$NON-NLS-1$
	private static final String PROPERTIES_PROPERTY = "properties"; //$NON-NLS-1$

	private List<String> _parentTypes;
	private List<PropertyElement> _properties;
	private boolean _serializeProperties;

	/**
	 * TypeElement
	 */
	public TypeElement()
	{
	}

	/**
	 * addParentType
	 * 
	 * @param type
	 */
	public void addParentType(String type)
	{
		if (type != null && type.length() > 0)
		{
			if (this._parentTypes == null)
			{
				this._parentTypes = new ArrayList<String>();
			}

			if (this._parentTypes.contains(type) == false)
			{
				this._parentTypes.add(type);
			}
		}
	}

	/**
	 * addProperty
	 * 
	 * @param property
	 */
	public void addProperty(PropertyElement property)
	{
		if (property != null)
		{
			if (this._properties == null)
			{
				this._properties = new ArrayList<PropertyElement>();
			}

			int index = this.getPropertyIndex(property.getName());

			if (index >= 0)
			{
				// replace existing property with the same name
				this._properties.set(index, property);
			}
			else
			{
				// add to the end of our list
				this._properties.add(property);
			}

			property.setOwningType(this.getName());
		}
	}

	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<PropertyElement> getProperties()
	{
		return CollectionsUtil.getListValue(this._properties);
	}

	/**
	 * getProperty
	 * 
	 * @param name
	 * @return
	 */
	public PropertyElement getProperty(String name)
	{
		int index = this.getPropertyIndex(name);
		PropertyElement result = null;

		if (index != -1)
		{
			result = this._properties.get(index);
		}

		return result;
	}

	/**
	 * getPropertyIndex
	 * 
	 * @param name
	 * @return
	 */
	protected int getPropertyIndex(String name)
	{
		int result = -1;

		if (name != null && name.length() > 0 && this._properties != null)
		{
			for (int i = 0; i < this._properties.size(); i++)
			{
				PropertyElement property = this._properties.get(i);

				if (name.equals(property.getName()))
				{
					result = i;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * getParentTypes
	 * 
	 * @return
	 */
	public List<String> getParentTypes()
	{
		return CollectionsUtil.getListValue(this._parentTypes);
	}

	/**
	 * getSerializeProperties
	 * 
	 * @return
	 */
	public boolean getSerializeProperties()
	{
		return this._serializeProperties;
	}

	/**
	 * hasParentTypes
	 * 
	 * @return
	 */
	public boolean hasParentTypes()
	{
		return this._parentTypes != null && this._parentTypes.isEmpty() == false;
	}

	/**
	 * hasProperties
	 * 
	 * @return
	 */
	public boolean hasProperties()
	{
		return this._properties != null && this._properties.isEmpty() == false;
	}

	/**
	 * setSerializeProperties
	 * 
	 * @param value
	 */
	public void setSerializeProperties(boolean value)
	{
		this._serializeProperties = value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		super.fromJSON(object);

		if (object.containsKey(PROPERTIES_PROPERTY))
		{
			List<PropertyElement> properties = IndexUtil.createList(object.get(PROPERTIES_PROPERTY), PropertyElement.class);

			for (PropertyElement property : properties)
			{
				this.addProperty(property);
			}
		}

		if (object.containsKey(FUNCTIONS_PROPERTY))
		{
			List<PropertyElement> functions = IndexUtil.createList(object.get(FUNCTIONS_PROPERTY), PropertyElement.class);

			for (PropertyElement function : functions)
			{
				this.addProperty(function);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#toJSON(org.mortbay.util.ajax.JSON.Output)
	 */
	@Override
	public void toJSON(Output out)
	{
		super.toJSON(out);

		if (this._serializeProperties)
		{
			List<PropertyElement> properties = new ArrayList<PropertyElement>(this.getProperties());
			List<FunctionElement> functions = new ArrayList<FunctionElement>();

			for (PropertyElement property : properties)
			{
				if (property instanceof FunctionElement)
				{
					functions.add((FunctionElement) property);
				}
			}

			properties.removeAll(functions);

			out.add(PROPERTIES_PROPERTY, properties);
			out.add(FUNCTIONS_PROPERTY, functions);
		}
	}

	/**
	 * toSource
	 * 
	 * @return
	 */
	public String toSource()
	{
		SourcePrinter printer = new SourcePrinter();

		this.toSource(printer);

		return printer.toString();
	}

	/**
	 * toSource
	 * 
	 * @param printer
	 */
	public void toSource(SourcePrinter printer)
	{
		printer.print(this.getName());

		if (this.hasParentTypes())
		{
			printer.print(" : ").print(StringUtil.join(", ", this.getParentTypes())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		printer.println().print("{").increaseIndent().println(); //$NON-NLS-1$

		for (PropertyElement property : this.getProperties())
		{
			property.toSource(printer);
			printer.println(";"); //$NON-NLS-1$
		}

		printer.decreaseIndent().println("}"); //$NON-NLS-1$
	}
}
