/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.model;

import com.aptana.samples.handlers.ISampleProjectHandler;

public interface IProjectSample
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
	 * @return the remote git url or the local zip path
	 */
	public String getLocation();

	/**
	 * @return the array of nature ids that should apply to the project
	 */
	public String[] getNatures();

	/**
	 * @return a class that does any necessary post-processing after the project is created; could be null
	 */
	public ISampleProjectHandler getProjectHandler();
}
