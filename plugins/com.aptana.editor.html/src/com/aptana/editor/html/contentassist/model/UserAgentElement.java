package com.aptana.editor.html.contentassist.model;

import com.aptana.core.util.StringUtil;

public class UserAgentElement
{
	private static final String EMPTY = "";

	private String _platform;
	private String _version;

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
					StringUtil.areEqual(this.getPlatform(), that.getPlatform()) //
				&&	StringUtil.areEqual(this.getVersion(), that.getVersion()); //
		}

		return result;
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

		String[] items = new String[] { //
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
	 * getPlatform
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
