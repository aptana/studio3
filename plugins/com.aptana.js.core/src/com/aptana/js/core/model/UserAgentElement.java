/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.model;

import java.util.HashMap;
import java.util.Map;

import com.aptana.jetty.util.epl.ajax.JSON.Convertible;
import com.aptana.jetty.util.epl.ajax.JSON.Output;

import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.StringUtil;

public class UserAgentElement implements Convertible
{
	private static final String DESCRIPTION_PROPERTY = "description"; //$NON-NLS-1$
	private static final String OS_VERSION_PROPERTY = "osVersion"; //$NON-NLS-1$
	private static final String OS_PROPERTY = "os"; //$NON-NLS-1$
	private static final String VERSION_PROPERTY = "version"; //$NON-NLS-1$
	private static final String PLATFORM_PROPERTY = "platform"; //$NON-NLS-1$

	/**
	 * Cache used to re-use UserAgentElement instances, when possible. We go ahead and create an instance here to
	 * simplify code later.
	 */
	private static final Map<Integer, UserAgentElement> INSTANCE_CACHE = new HashMap<Integer, UserAgentElement>();

	private String _platform;
	private String _version;
	private String _os;
	private String _osVersion;
	private String _description;

	/**
	 * Clear the user agent element cache.
	 */
	public static void clearCache()
	{
		INSTANCE_CACHE.clear();
	}

	/**
	 * Create a new instance of a UserAgentElement. This method uses cached instances when possible.
	 * 
	 * @param map
	 * @return
	 */
	static UserAgentElement createUserAgentElement(Map<?, ?> map)
	{
		String platform = StringUtil.getStringValue(map.get(PLATFORM_PROPERTY));
		String version = StringUtil.getStringValue(map.get(VERSION_PROPERTY));
		String os = StringUtil.getStringValue(map.get(OS_PROPERTY));
		String osVersion = StringUtil.getStringValue(map.get(OS_VERSION_PROPERTY));
		String description = StringUtil.getStringValue(map.get(DESCRIPTION_PROPERTY));

		return createUserAgentElement(platform, version, os, osVersion, description);
	}

	/**
	 * Create a new instance of a UserAgentElement. This method uses cached instances when possible.
	 * 
	 * @param platform
	 * @return
	 */
	public static UserAgentElement createUserAgentElement(String platform)
	{
		return createUserAgentElement(platform, StringUtil.EMPTY, StringUtil.EMPTY, StringUtil.EMPTY, StringUtil.EMPTY);
	}

	/**
	 * Create a new instance of a UserAgentElement. This method uses cached instances when possible.
	 * 
	 * @param platform
	 * @param version
	 * @param os
	 * @param osVersion
	 * @param description
	 * @return
	 */
	public static UserAgentElement createUserAgentElement(String platform, String version, String os, String osVersion,
			String description)
	{
		UserAgentElement result;

		// calculate hash code as if we already have an instance
		int hashCode = getHashCode(platform, version, os, osVersion, description);

		if (INSTANCE_CACHE.containsKey(hashCode))
		{
			// use cached instance
			result = INSTANCE_CACHE.get(hashCode);
		}
		else
		{
			// We have to create a new instance
			result = new UserAgentElement();
			result.setPlatform(platform);
			result.setVersion(version);
			result.setOS(os);
			result.setOSVersion(osVersion);
			result.setDescription(description);

			// cache for next time
			INSTANCE_CACHE.put(hashCode, result);
		}

		return result;
	}

	/**
	 * Calculate a hash code for a list of individual property values. Separating out the calculation this way allows
	 * calculation of a hash code without having to create a new UserAgentElement. This is useful for caching purposes.
	 * 
	 * @param platform
	 * @param version
	 * @param os
	 * @param osVersion
	 * @param description
	 * @return
	 */
	private static int getHashCode(String platform, String version, String os, String osVersion, String description)
	{
		int h = 0;

		h = ((platform != null) ? platform.hashCode() : 0);
		h = h * 31 + ((version != null) ? version.hashCode() : 0);
		h = h * 31 + ((os != null) ? os.hashCode() : 0);
		h = h * 31 + ((osVersion != null) ? osVersion.hashCode() : 0);
		h = h * 31 + ((description != null) ? description.hashCode() : 0);

		return h;
	}

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
		this.setPlatform(StringUtil.getStringValue(object.get(PLATFORM_PROPERTY)));
		this.setVersion(StringUtil.getStringValue(object.get(VERSION_PROPERTY)));
		this.setOS(StringUtil.getStringValue(object.get(OS_PROPERTY)));
		this.setOSVersion(StringUtil.getStringValue(object.get(OS_VERSION_PROPERTY)));
		this.setDescription(StringUtil.getStringValue(object.get(DESCRIPTION_PROPERTY)));
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return getHashCode(_platform, _version, _os, _osVersion, _description);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof UserAgentElement)
		{
			UserAgentElement that = (UserAgentElement) obj;

			// @formatter:off
			return ObjectUtil.areEqual(_platform, that._platform)
				&& ObjectUtil.areEqual(_version, that._version)
				&& ObjectUtil.areEqual(_os, that._os)
				&& ObjectUtil.areEqual(_osVersion, that._osVersion)
				&& ObjectUtil.areEqual(_description, that._description);
			// @formatter:on
		}
		else
		{
			return super.equals(obj);
		}
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
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		out.add(PLATFORM_PROPERTY, this.getPlatform());
		out.add(VERSION_PROPERTY, this.getVersion());
		out.add(OS_PROPERTY, this.getOS());
		out.add(OS_VERSION_PROPERTY, this.getOSVersion());
		out.add(DESCRIPTION_PROPERTY, this.getDescription());
	}

	public String toString()
	{
		return this.getPlatform() + "[" + this.getVersion() + "]:" + this.getOS();
	}
}
