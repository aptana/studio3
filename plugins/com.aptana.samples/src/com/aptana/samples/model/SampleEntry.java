/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class SampleEntry
{

	private final File file;
	private final Object parent;
	private final boolean isRoot;

	private List<SampleEntry> subEntries;

	/**
	 * Creates a new sample entry.
	 * 
	 * @param file
	 *            the sample file
	 * @param parent
	 *            the parent of this entry
	 * @param isRoot
	 *            true if the entry is the root, false otherwise
	 */
	public SampleEntry(File file, Object parent, boolean isRoot)
	{
		this.file = file;
		this.parent = parent;
		this.isRoot = isRoot;
	}

	public File getFile()
	{
		return file;
	}

	public Object getParent()
	{
		return parent;
	}

	public boolean isRoot()
	{
		return isRoot;
	}

	public SampleEntry[] getSubEntries()
	{
		if (subEntries == null)
		{
			subEntries = new ArrayList<SampleEntry>();
			if (file != null)
			{
				File[] files = file.listFiles();
				if (files != null)
				{
					for (File file : files)
					{
						subEntries.add(new SampleEntry(file, this, false));
					}
				}
			}
		}
		return subEntries.toArray(new SampleEntry[subEntries.size()]);
	}
}
