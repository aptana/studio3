/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
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

	@Override
	public String toString()
	{
		return getName();
	}
}
