package com.aptana.editor.js.contentassist.model;

public class UserAgentElement
{
	private String _platform;
	private String _version;
	private String _os;
	private String _osVersion;
	private String _description;

	/**
	 * UserAgentElelment
	 */
	public UserAgentElement()
	{
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
