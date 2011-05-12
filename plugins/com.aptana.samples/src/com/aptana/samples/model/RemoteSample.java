/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.model;

public class RemoteSample implements ISample
{

	private SamplesReference samplesRef;

	public RemoteSample(SamplesReference samplesRef)
	{
		this.samplesRef = samplesRef;
	}

	public boolean isRemote()
	{
		return true;
	}

	public String getName()
	{
		return samplesRef.getName();
	}

	public String getPath()
	{
		return samplesRef.getPath();
	}

	public SamplesReference getReference()
	{
		return samplesRef;
	}

	public SampleEntry getRootEntry()
	{
		return null;
	}
}
