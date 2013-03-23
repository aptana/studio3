/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.model;

import java.util.Map;

import com.aptana.jetty.util.epl.ajax.JSON.Convertible;
import com.aptana.jetty.util.epl.ajax.JSON.Output;

import com.aptana.core.util.StringUtil;

public class ExceptionElement implements Convertible
{
	private static final String DESCRIPTION_PROPERTY = "description"; //$NON-NLS-1$
	private static final String TYPE_PROPERTY = "type"; //$NON-NLS-1$

	private String _type;
	private String _description;

	/**
	 * ExceptionElement
	 */
	public ExceptionElement()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		this.setType(StringUtil.getStringValue(object.get(TYPE_PROPERTY)));
		this.setDescription(StringUtil.getStringValue(object.get(DESCRIPTION_PROPERTY)));
	}

	/**
	 * getDescription
	 */
	public String getDescription()
	{
		return StringUtil.getStringValue(this._description);
	}

	/**
	 * getType
	 */
	public String getType()
	{
		return StringUtil.getStringValue(this._type);
	}

	/**
	 * setDescription
	 */
	public void setDescription(String description)
	{
		this._description = description;
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		out.add(TYPE_PROPERTY, this.getType());
		out.add(DESCRIPTION_PROPERTY, this.getDescription());
	}
}
