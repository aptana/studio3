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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;

public class ElementElement
{
	private String _name;
	private String _displayName;
	private Map<String, AttributeElement> _attributes;
	private String _description;

	/**
	 * ElementElement
	 */
	public ElementElement()
	{
	}

	/**
	 * Adds an attribute consisting solely of a name. Attributes with descriptions/values should be added using
	 * {@link #addAttribute(AttributeElement)}
	 * 
	 * @param attribute
	 *            the attribute to add
	 */
	public void addAttribute(String attribute)
	{
		AttributeElement ae = new AttributeElement();
		ae.setName(attribute);
		addAttribute(ae);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean result = false;

		if (obj instanceof ElementElement)
		{
			ElementElement that = (ElementElement) obj;

			result = //
			this.getName().equals(that.getName()) //
					&& this.getDisplayName().equals(that.getDisplayName()) //
					&& this.getDescription().equals(that.getDescription()) //
					&& this.getAttributes().equals(that.getAttributes());
		}

		return result;
	}

	/**
	 * getAttributes
	 * 
	 * @return the attributes
	 */
	public synchronized List<String> getAttributes()
	{
		if (CollectionsUtil.isEmpty(this._attributes))
		{
			return Collections.emptyList();
		}
		return new ArrayList<String>(this._attributes.keySet());
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
	 * getDisplayName
	 * 
	 * @return the displayName
	 */
	public String getDisplayName()
	{
		return (this._displayName == null) ? StringUtil.EMPTY : this._displayName;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int h = this.getName().hashCode();

		h = 31 * h + this.getDisplayName().hashCode();
		h = 31 * h + this.getDescription().hashCode();
		h = 31 * h + this.getAttributes().hashCode();

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
	 * setDisplayName
	 * 
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName)
	{
		this._displayName = displayName;
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

	/**
	 * @param attributeName
	 * @return
	 */
	public synchronized AttributeElement getAttribute(String attributeName)
	{
		if (StringUtil.isEmpty(attributeName) || CollectionsUtil.isEmpty(this._attributes))
		{
			return null;
		}

		return this._attributes.get(attributeName);
	}

	/**
	 * Adds an attribute to our map. Note that attributes are stored by name, so the last one added for a given name
	 * "wins".
	 * 
	 * @param ae
	 * @return boolean indicating if we added the attribute.
	 */
	public synchronized boolean addAttribute(AttributeElement ae)
	{
		if (ae == null)
		{
			return false;
		}
		ae.setElement(getName());
		if (this._attributes == null)
		{
			this._attributes = new HashMap<String, AttributeElement>();
		}

		this._attributes.put(ae.getName(), ae);
		return true;
	}
}
