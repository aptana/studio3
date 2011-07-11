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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0)
	{
		boolean result = false;

		if (arg0 instanceof BuildPathEntry)
		{
			BuildPathEntry other = (BuildPathEntry) arg0;

			result = displayName.equals(other.displayName) && path.equals(other.path);
		}

		return result;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return displayName.hashCode() * 31 + path.hashCode();
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
