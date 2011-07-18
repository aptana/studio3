/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.buildpath.core;

import java.net.URI;

/**
 * A BuildPathEntry represents a single item in the Project Build Path preference page for a project. It is comprised of
 * a display name, used in the UI, and a path, a URI to a resource. It is expected that the URI points to a file type
 * that can be indexed.
 */
public class BuildPathEntry
{
	private boolean selected;
	private String displayName;
	private URI path;

	/**
	 * BuildPathEntry
	 * 
	 * @param displayName
	 * @param path
	 */
	public BuildPathEntry(String displayName, URI path)
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
	public URI getPath()
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return displayName + ":" + path.toString(); //$NON-NLS-1$
	}
}
