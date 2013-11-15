/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core;

public interface IUserAgentManager
{

	public static final IUserAgent[] NO_USER_AGENTS = new IUserAgent[0];

	// TODO Refactor to return a Set
	public IUserAgent[] getAllUserAgents();

	// TODO Refactor to return a Set
	public IUserAgent[] getDefaultUserAgents(String natureID);

	public IUserAgent getUserAgentById(String id);

	public boolean addUserAgent(IUserAgent agent);
}
