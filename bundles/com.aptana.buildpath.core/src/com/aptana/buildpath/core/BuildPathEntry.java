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
public class BuildPathEntry implements IBuildPathEntry
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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		BuildPathEntry other = (BuildPathEntry) obj;
		if (path == null)
		{
			if (other.path != null)
			{
				return false;
			}
		}
		else if (!path.equals(other.path))
		{
			return false;
		}
		return true;
	}
}
