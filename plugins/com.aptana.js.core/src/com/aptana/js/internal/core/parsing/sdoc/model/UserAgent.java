/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc.model;

/**
 * UserAgent
 */
public class UserAgent extends Tag
{
	private String _name;
	private String _version;

	/**
	 * UserAgent
	 */
	public UserAgent()
	{
		super(TagType.USER_AGENT);
	}

	/**
	 * getName
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * getVersion
	 * 
	 * @return the version
	 */
	public String getVersion()
	{
		return this._version;
	}

	/**
	 * setName
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this._name = name;
	}

	/**
	 * setVersion
	 * 
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version)
	{
		this._version = version;
	}
}
