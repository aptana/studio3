/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.core;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

import com.aptana.core.IFilter;

/**
 * Manages IServers. Allows adding/removing and finding by name. Deals with persisting the servers under the hood.
 * 
 * @author cwilliams
 */
public interface IServerManager
{

	/**
	 * Add a server instance to be persisted.
	 * 
	 * @param server
	 */
	public void add(IServer server);

	/**
	 * Remove a server instance.
	 * 
	 * @param server
	 */
	public void remove(IServer server);

	/**
	 * Grab the list of servers.
	 * 
	 * @return
	 */
	public List<IServer> getServers();

	/**
	 * Grab the list of servers matching the filter.
	 * 
	 * @param filter
	 * @return
	 */
	public List<IServer> getServers(IFilter<IServer> filter);

	/**
	 * Add changes listener
	 * 
	 * @param listener
	 */
	public void addServerChangeListener(IServerChangeListener listener);

	/**
	 * Remove changes listener
	 * 
	 * @param listener
	 */
	public void removeServerChangeListener(IServerChangeListener listener);

	/**
	 * @param name
	 * @return
	 */
	public IServer findServerByName(String name);

	/**
	 * Grab the list of IServerTypes. TODO make it a Set?
	 * 
	 * @return
	 */
	public List<IServerType> getServerTypes();

	/**
	 * FIXME Move this onto IServerType? A type should be a factory for instances of IServer.
	 * 
	 * @param typeId
	 * @return
	 * @throws CoreException
	 */
	public IServer createServer(String typeId) throws CoreException;
}
