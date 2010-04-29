package com.aptana.editor.css.contentassist.model;

public class UserAgentElement
{
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
}
