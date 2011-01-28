/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.contentassist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElementElement
{
	private static final String EMPTY = ""; //$NON-NLS-1$

	private String _name;
	private String _displayName;
	private List<String> _attributes;
	private String _description;

	/**
	 * ElementElement
	 */
	public ElementElement()
	{
	}

	/**
	 * addAttribute
	 * 
	 * @param attribute
	 *            the attribute to add
	 */
	public void addAttribute(String attribute)
	{
		if (this._attributes == null)
		{
			this._attributes = new ArrayList<String>();
		}

		this._attributes.add(attribute);
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
	public List<String> getAttributes()
	{
		List<String> result = Collections.emptyList();

		if (this._attributes != null)
		{
			result = this._attributes;
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
		return (this._description == null) ? EMPTY : this._description;
	}

	/**
	 * getDisplayName
	 * 
	 * @return the displayName
	 */
	public String getDisplayName()
	{
		return (this._displayName == null) ? EMPTY : this._displayName;
	}

	/**
	 * getName
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return (this._name == null) ? EMPTY : this._name;
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
}
