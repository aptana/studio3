/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core;

public interface IUserAgent extends Comparable<IUserAgent>
{

	/**
	 * Unique identifier of this user agent.
	 * 
	 * @return
	 */
	public String getID();

	/**
	 * Display name of this user agent.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Returns the path to the enabled icon. Used in conjunction with {@link #getContributor()} to load the image.
	 * 
	 * @return
	 */
	public String getEnabledIconPath();

	/**
	 * Returns the path to the disabled icon. Used in conjunction with {@link #getContributor()} to load the image.
	 * 
	 * @return
	 */
	public String getDisabledIconPath();

	/**
	 * Returns the contributing bundle's id.
	 * 
	 * @return
	 */
	public String getContributor();
}
