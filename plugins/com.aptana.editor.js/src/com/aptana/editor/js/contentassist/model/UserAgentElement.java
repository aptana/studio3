package com.aptana.editor.js.contentassist.model;


public class UserAgentElement
{
	private static final String EMPTY = "";
	
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
			
			result =
				this.getDescription().equals(that.getDescription())
			&&	this.getOS().equals(that.getOS())
			&&	this.getOSVersion().equals(that.getOSVersion())
			&&	this.getPlatform().equals(that.getPlatform())
			&&	this.getVersion().equals(that.getVersion());
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
		return (this._description != null) ? this._description : EMPTY;
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
		return (this._os != null) ? this._os : EMPTY;
	}

	/**
	 * getOSVersion
	 * 
	 * @return
	 */
	public String getOSVersion()
	{
		return (this._osVersion != null) ? this._osVersion : EMPTY;
	}

	/**
	 * getPlatform
	 * 
	 * @return
	 */
	public String getPlatform()
	{
		return (this._platform != null) ? this._platform : EMPTY;
	}

	/**
	 * getVersion
	 * 
	 * @return
	 */
	public String getVersion()
	{
		return (this._version != null) ? this._version : EMPTY;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int h = 0;
		
		String[] items = new String[] {
			this.getDescription(),
			this.getOS(),
			this.getOSVersion(),
			this.getPlatform(),
			this.getVersion()
		};
		
		for (String item : items)
		{
			if (item != null)
			{
				h = 31*h + item.hashCode();
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
}
