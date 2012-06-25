/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist.model;

import java.util.Map;

import com.aptana.jetty.util.epl.ajax.JSON.Convertible;
import com.aptana.jetty.util.epl.ajax.JSON.Output;

import com.aptana.core.util.StringUtil;

public class SpecificationElement implements Convertible
{
	private static final String VERSION_PROPERTY = "version"; //$NON-NLS-1$
	private static final String NAME_PROPERTY = "name"; //$NON-NLS-1$

	private String _name;
	private String _version;

	/**
	 * SpecificationElement
	 */
	public SpecificationElement()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		this.setName(StringUtil.getStringValue(object.get(NAME_PROPERTY)));
		this.setVersion(StringUtil.getStringValue(object.get(VERSION_PROPERTY)));
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
	 * getVersion
	 * 
	 * @return
	 */
	public String getVersion()
	{
		return StringUtil.getStringValue(this._version);
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
	 * setVersion
	 * 
	 * @param version
	 */
	public void setVersion(String version)
	{
		this._version = version;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		out.add(NAME_PROPERTY, this.getName());
		out.add(VERSION_PROPERTY, this.getVersion());
	}
}
