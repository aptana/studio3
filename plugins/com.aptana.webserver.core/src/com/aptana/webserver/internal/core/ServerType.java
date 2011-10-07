/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.internal.core;

import com.aptana.webserver.core.IServerType;

/**
 * TODO Define any more properties on types of servers here? i.e. supportsLaunchMode(String mode), or
 * createServer(IServerConfiguration config) ?
 * 
 * @author cwilliams
 */
public final class ServerType implements IServerType
{
	private String id;
	private String name;

	public ServerType(String id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}
}