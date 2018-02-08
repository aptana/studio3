/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.model;

import java.text.MessageFormat;
import java.util.Map;

import com.aptana.jetty.util.epl.ajax.JSON.Convertible;
import com.aptana.jetty.util.epl.ajax.JSON.Output;

import com.aptana.core.util.StringUtil;

public class UserAgentElement implements Convertible
{
	private static final String DESCRIPTION_PROPERTY = "description"; //$NON-NLS-1$
	private static final String VERSION_PROPERTY = "version"; //$NON-NLS-1$
	private static final String PLATFORM_PROPERTY = "platform"; //$NON-NLS-1$
	private static final String OS_PROPERTY = "os"; //$NON-NLS-1$

	private String _description;
	private String _os;
	private String _platform;
	private String _version;

	/**
	 * UserAgentElement
	 */
	public UserAgentElement()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		setOS(StringUtil.getStringValue(object.get(OS_PROPERTY)));
		setPlatform(StringUtil.getStringValue(object.get(PLATFORM_PROPERTY)));
		setVersion(StringUtil.getStringValue(object.get(VERSION_PROPERTY)));
		setDescription(StringUtil.getStringValue(object.get(DESCRIPTION_PROPERTY)));
	}

	/**
	 * getDescription;
	 */
	public String getDescription()
	{
		return StringUtil.getStringValue(this._description);
	}

	/**
	 * getOS
	 * 
	 * @return
	 */
	public String getOS()
	{
		return StringUtil.getStringValue(this._os);
	}

	/**
	 * getPlatform
	 * 
	 * @return
	 */
	public String getPlatform()
	{
		return StringUtil.getStringValue(this._platform);
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
	 * setDescription
	 * 
	 * @param description
	 */
	public void setDescription(String description)
	{
		this._description = description;
	}

	/**
	 * setOS
	 * 
	 * @param os
	 */
	public void setOS(String os)
	{
		this._os = os;
	}

	/**
	 * setPlatform
	 * 
	 * @param platform
	 */
	public void setPlatform(String platform)
	{
		this._platform = platform;
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
		out.add(OS_PROPERTY, getOS());
		out.add(PLATFORM_PROPERTY, getPlatform());
		out.add(VERSION_PROPERTY, getVersion());
		out.add(DESCRIPTION_PROPERTY, getDescription());
	}

	public String toString()
	{
		return MessageFormat.format("{0}[{1}]:{2}", getPlatform(), getVersion(), getOS()); //$NON-NLS-1$
	}
}
