/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.browser.internal;

import java.util.ArrayList;
import java.util.List;

public class BrowserSizeCategory implements Comparable<BrowserSizeCategory>
{

	private final String id;
	private final String name;
	private final int order;
	private final List<BrowserSize> sizes;

	public BrowserSizeCategory(String id, String name, int order)
	{
		this.id = id;
		this.name = name;
		this.order = order;
		sizes = new ArrayList<BrowserSize>();
	}

	public void addSize(BrowserSize size)
	{
		sizes.add(size);
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public int getOrder()
	{
		return order;
	}

	public BrowserSize[] getSizes()
	{
		return sizes.toArray(new BrowserSize[sizes.size()]);
	}

	public int compareTo(BrowserSizeCategory o)
	{
		if (order != o.order)
		{
			return order - o.order;
		}
		return name.compareTo(o.name);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
		if (!(obj instanceof BrowserSizeCategory))
		{
			return false;
		}
		BrowserSizeCategory other = (BrowserSizeCategory) obj;
		if (id == null)
		{
			if (other.id != null)
			{
				return false;
			}
		}
		else if (!id.equals(other.id))
		{
			return false;
		}
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
