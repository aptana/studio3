/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.browser.internal;

public class BrowserSize
{

	private final String name;
	private final int width;
	private final int height;
	private final BrowserSizeCategory category;
	private final BrowserBackgroundImage image;

	public BrowserSize(String name, int width, int height, BrowserSizeCategory category, BrowserBackgroundImage image)
	{
		this.name = name;
		this.width = width;
		this.height = height;
		this.category = category;
		this.image = image;
	}

	public String getName()
	{
		return name;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public BrowserSizeCategory getCategory()
	{
		return category;
	}

	public BrowserBackgroundImage getImage()
	{
		return image;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
