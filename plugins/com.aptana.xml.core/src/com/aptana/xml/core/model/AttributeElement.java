/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.core.util.StringUtil;

public class AttributeElement
{
	private String _name;
	private String _element;
	private String _description;
	private List<ValueElement> _values;

	/**
	 * AttributeElement
	 */
	public AttributeElement()
	{
	}

	/**
	 * addValue
	 * 
	 * @param values
	 *            the value to add
	 */
	public void addValue(ValueElement value)
	{
		if (this._values == null)
		{
			this._values = new ArrayList<ValueElement>();
		}

		this._values.add(value);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean result = false;

		if (obj instanceof AttributeElement)
		{
			AttributeElement that = (AttributeElement) obj;

			result = //
			this.getName().equals(that.getName()) //
					&& this.getElement().equals(that.getElement()) //
					&& this.getDescription().equals(that.getDescription()) //
					&& this.getValues().equals(that.getValues());
		}

		return result;
	}

	/**
	 * getDescription
	 * 
	 * @return the description
	 */
	public String getDescription()
	{
		return (this._description == null) ? StringUtil.EMPTY : this._description;
	}

	/**
	 * getElement
	 * 
	 * @return
	 */
	public String getElement()
	{
		return (this._element == null) ? StringUtil.EMPTY : this._element;
	}

	/**
	 * getName
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return (this._name == null) ? StringUtil.EMPTY : this._name;
	}

	/**
	 * getValues
	 * 
	 * @return the values
	 */
	public List<ValueElement> getValues()
	{
		List<ValueElement> result = Collections.emptyList();

		if (this._values != null)
		{
			result = this._values;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int h = this.getName().hashCode();

		h = 31 * h + this.getElement().hashCode();
		h = 31 * h + this.getDescription().hashCode();
		h = 31 * h + this.getValues().hashCode();

		return h;
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
	 * setElement
	 * 
	 * @param element
	 */
	public void setElement(String element)
	{
		this._element = element;
	}

	/**
	 * setName
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this._name = name;
	}
}
