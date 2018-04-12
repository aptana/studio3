/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.model;

import com.aptana.core.util.StringUtil;

public class AliasElement
{
	private String _name;
	private String _type;
	private String _description;

	/**
	 * AliasElement
	 */
	public AliasElement()
	{
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return this._description;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return StringUtil.getStringValue(this._name);
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public String getType()
	{
		return StringUtil.getStringValue(this._type);
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
	 * setName
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this._name = name;
	}

	/**
	 * setType
	 * 
	 * @param type
	 */
	public void setType(String type)
	{
		this._type = type;
	}
}
