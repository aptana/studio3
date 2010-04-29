package com.aptana.editor.html.contentassist.model;

public class UserAgentElement
{
	private String _platform;
	private String _version;

	/**
	 * UserAgentElement
	 */
	public UserAgentElement()
	{
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

	public void setPlatform(String platform)
	{
		this._platform = platform;
	}

	public void setVersion(String version)
	{
		this._version = version;
	}
}
