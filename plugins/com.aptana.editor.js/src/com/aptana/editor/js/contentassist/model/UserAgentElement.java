package com.aptana.editor.js.contentassist.model;

import com.aptana.core.util.StringUtil;

public class UserAgentElement
{
	private String _platform;
	private String _version;
	private String _os;
	private String _osVersion;
	private String _description;
	private int _hash;
	private String _key; // used by the indexing system only

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
			
			result =
				StringUtil.areEqual(this.getDescription(), that.getDescription())
			&&	StringUtil.areEqual(this.getOS(), that.getOS())
			&&	StringUtil.areEqual(this.getOSVersion(), that.getOSVersion())
			&&	StringUtil.areEqual(this.getPlatform(), that.getPlatform())
			&&	StringUtil.areEqual(this.getVersion(), that.getVersion());
		}
		
		return result;
	}

	/**
	 * getDescription
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return this._description;
	}

	/**
	 * getKey
	 * 
	 * @return
	 */
	public String getKey()
	{
		return this._key;
	}

	/**
	 * getOS
	 * 
	 * @return
	 */
	public String getOS()
	{
		return this._os;
	}

	/**
	 * getOSVersion
	 * 
	 * @return
	 */
	public String getOSVersion()
	{
		return this._osVersion;
	}

	/**
	 * getPlatform
	 * 
	 * @return
	 */
	public String getPlatform()
	{
		return this._platform;
	}

	/**
	 * getVersion
	 * 
	 * @return
	 */
	public String getVersion()
	{
		return this._version;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int h = this._hash;
		
		if (h == 0)
		{
			String[] items = new String[] {
				this._description,
				this._os,
				this._osVersion,
				this._platform,
				this._version
			};
			
			for (String item : items)
			{
				if (item != null)
				{
					h = 31*h + item.hashCode();
				}
			}
			
			this._hash = h;
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
	 * setKey
	 * 
	 * @param key
	 */
	public void setKey(String key)
	{
		this._key = key;
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
}
