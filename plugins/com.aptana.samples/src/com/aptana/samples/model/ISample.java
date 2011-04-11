/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.model;

public interface ISample
{

	/**
	 * @return true if the sample is from a git repo, false if it resides locally
	 */
	public boolean isRemote();

	/**
	 * @return the name of the sample
	 */
	public String getName();

	/**
	 * @return the remote git path, or null if the sample is local
	 */
	public String getPath();

	/**
	 * @return this sample's reference that contains additional information
	 */
	public SamplesReference getReference();

	/**
	 * @return the root entry for the local sample, or null if the sample is remote
	 */
	public SampleEntry getRootEntry();
}
