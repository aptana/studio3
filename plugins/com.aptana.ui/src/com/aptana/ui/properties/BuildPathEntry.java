/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.properties;

/**
 * BuildPathEntry
 */
public class BuildPathEntry
{
	private boolean selected;
	private String displayName;
	private String path;

	/**
	 * BuildPathEntry
	 * 
	 * @param displayName
	 * @param path
	 */
	public BuildPathEntry(String displayName, String path)
	{
		this.displayName = displayName;
		this.path = path;
	}

	/**
	 * getDisplayName
	 * 
	 * @return
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * getPath
	 * 
	 * @return
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * isSelected
	 * 
	 * @return
	 */
	public boolean isSelected()
	{
		return selected;
	}

	/**
	 * setSelected
	 * 
	 * @param value
	 */
	public void setSelected(boolean value)
	{
		selected = value;
	}
}
