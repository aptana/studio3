/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean result = false;

		if (this == obj)
		{
			result = true;
		}
		else
		{
			UserAgentElement that = (UserAgentElement) obj;

			result = //
			this.getDescription().equals(that.getDescription()) //
				&& this.getOS().equals(that.getOS()) //
				&& this.getOSVersion().equals(that.getOSVersion()) //
				&& this.getPlatform().equals(that.getPlatform()) //
				&& this.getVersion().equals(that.getVersion()); //
		}

		return result;
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
	 * getKey
	 * 
	 * @return
	 */
	public String getKey()
	{
		return Integer.toString(this.hashCode());
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int h = 0;

		String[] items = new String[] { //
		this.getDescription(), //
			this.getOS(), //
			this.getOSVersion(), //
			this.getPlatform(), //
			this.getVersion() //
		};

		for (String item : items)
		{
			if (item != null)
			{
				h = 31 * h + item.hashCode();
			}
		}

		return h;
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
