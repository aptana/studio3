/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import java.util.Map;

import org.mortbay.util.ajax.JSON.Convertible;
import org.mortbay.util.ajax.JSON.Output;

import com.aptana.core.util.StringUtil;

public class UserAgentElement implements Convertible
{
	private static final String DESCRIPTION_PROPERTY = "description"; //$NON-NLS-1$
	private static final String OS_VERSION_PROPERTY = "osVersion"; //$NON-NLS-1$
	private static final String OS_PROPERTY = "os"; //$NON-NLS-1$
	private static final String VERSION_PROPERTY = "version"; //$NON-NLS-1$
	private static final String PLATFORM_PROPERTY = "platform"; //$NON-NLS-1$

	private String _platform;
	private String _version;
	private String _os;
	private String _osVersion;
	private String _description;

	/**
	 * UserAgentElement
	 */
	public UserAgentElement()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.mortbay.util.ajax.JSON.Convertible#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		this.setPlatform(StringUtil.getStringValue(object.get(PLATFORM_PROPERTY)));
		this.setVersion(StringUtil.getStringValue(object.get(VERSION_PROPERTY)));
		this.setOS(StringUtil.getStringValue(object.get(OS_PROPERTY)));
		this.setOSVersion(StringUtil.getStringValue(object.get(OS_VERSION_PROPERTY)));
		this.setDescription(StringUtil.getStringValue(object.get(DESCRIPTION_PROPERTY)));
	}

	/**
	 * getDescription
	 * 
	 * @return
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
	 * getOSVersion
	 * 
	 * @return
	 */
	public String getOSVersion()
	{
		return StringUtil.getStringValue(this._osVersion);
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
	 * setOSVersion
	 * 
	 * @param OSVersion
	 */
	public void setOSVersion(String OSVersion)
	{
		this._osVersion = OSVersion;
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
	 * @see org.mortbay.util.ajax.JSON.Convertible#toJSON(org.mortbay.util.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		out.add(PLATFORM_PROPERTY, this.getPlatform());
		out.add(VERSION_PROPERTY, this.getVersion());
		out.add(OS_PROPERTY, this.getOS());
		out.add(OS_VERSION_PROPERTY, this.getOSVersion());
		out.add(DESCRIPTION_PROPERTY, this.getDescription());
	}
}
