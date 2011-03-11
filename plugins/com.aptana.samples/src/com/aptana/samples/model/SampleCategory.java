/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.model;

public class SampleCategory
{

	private final String id;
	private final String name;
	private String iconFile;

	public SampleCategory(String id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getIconFile()
	{
		return iconFile;
	}

	public void setIconFile(String iconFile)
	{
		this.iconFile = iconFile;
	}
}
