/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.buildpath.core;

import java.net.URI;

public interface IBuildPathEntry
{

	/**
	 * getDisplayName
	 * 
	 * @return
	 */
	public String getDisplayName();

	/**
	 * getPath
	 * 
	 * @return
	 */
	public URI getPath();

	/**
	 * isSelected
	 * 
	 * @return
	 */
	public boolean isSelected();

	/**
	 * setSelected
	 * 
	 * @param value
	 */
	public void setSelected(boolean value);

}