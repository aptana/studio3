package com.aptana.editor.css.contentassist.model;

import com.aptana.core.util.StringUtil;

public class UserAgentElement
{
	private String _description;
	private String _os;
	private String _platform;
	private String _version;
	private int _hash;

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
		else if (obj instanceof UserAgentElement)
		{
			UserAgentElement that = (UserAgentElement) obj;
			
			result =
				StringUtil.areEqual(this.getDescription(), ((UserAgentElement) obj).getDescription())
			&&	StringUtil.areEqual(this.getOS(), that.getOS())
			&&	StringUtil.areEqual(this.getPlatform(), that.getPlatform())
			&&	StringUtil.areEqual(this.getVersion(), that.getVersion());
		}
		
		return result;
	}

	/**
	 * getDescription;
	 */
	public String getDescription()
	{
		return this._description;
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
		this._hash = 0;
	}

	/**
	 * setOS
	 * 
	 * @param os
	 */
	public void setOS(String os)
	{
		this._os = os;
		this._hash = 0;
	}

	/**
	 * setPlatform
	 * 
	 * @param platform
	 */
	public void setPlatform(String platform)
	{
		this._platform = platform;
		this._hash = 0;
	}

	/**
	 * setVersion
	 * 
	 * @param version
	 */
	public void setVersion(String version)
	{
		this._version = version;
		this._hash = 0;
	}
}
