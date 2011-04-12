/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.model;

import java.io.File;

public class LocalSample implements ISample
{

	private SampleEntry sampleEntry;

	public LocalSample(SampleEntry sampleEntry)
	{
		this.sampleEntry = sampleEntry;
	}

	public boolean isRemote()
	{
		return false;
	}

	public String getName()
	{
		File file = sampleEntry.getFile();
		if (file == null)
		{
			return null;
		}
		String name = file.getName();
		if (name.endsWith(".zip")) //$NON-NLS-1$
		{
			return name.substring(0, name.length() - 4);
		}
		return name;
	}

	public String getPath()
	{
		return null;
	}

	public SamplesReference getReference()
	{
		Object parent = sampleEntry.getParent();
		while (parent instanceof SampleEntry)
		{
			parent = ((SampleEntry) parent).getParent();
		}
		if (parent instanceof SamplesReference)
		{
			return (SamplesReference) parent;
		}
		return null;
	}

	public SampleEntry getRootEntry()
	{
		return sampleEntry;
	}
}
