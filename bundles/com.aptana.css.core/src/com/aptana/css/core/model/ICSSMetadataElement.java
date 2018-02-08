/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.model;

import java.util.List;

public interface ICSSMetadataElement
{
	/**
	 * getDescription;
	 */
	public String getDescription();

	/**
	 * getExample
	 * 
	 * @return
	 */
	public String getExample();

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * getUserAgentNames
	 * 
	 * @return
	 */
	public List<String> getUserAgentNames();

	/**
	 * getUserAgents
	 * 
	 * @return
	 */
	public List<UserAgentElement> getUserAgents();
}