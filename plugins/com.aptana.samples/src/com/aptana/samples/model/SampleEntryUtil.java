/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.model;

public class SampleEntryUtil
{

	public static SamplesReference getParentSamplesRef(SampleEntry entry)
	{
		Object parent = entry.getParent();
		while (parent instanceof SampleEntry)
		{
			parent = ((SampleEntry) parent).getParent();
		}
		return (parent instanceof SamplesReference) ? (SamplesReference) parent : null;
	}

	public static SampleEntry getRootSample(SampleEntry entry)
	{
		Object parent = entry.getParent();
		while (parent instanceof SampleEntry)
		{
			entry = (SampleEntry) parent;
			parent = entry.getParent();
		}
		return entry;
	}
}
