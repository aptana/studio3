package com.aptana.editor.html.contentassist.model;

public class UserAgentElement
{
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

	public void setOS(String os)
	{
		this._os = os;
	}

	public void setOSVersion(String version)
	{
		this._osVersion = version;
	}

	public void setPlatform(String platform)
	{
		this._platform = platform;
	}

	public void setVersion(String version)
	{
		this._version = version;
	}
}
