/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.core;

import com.aptana.core.IURIMapper;
import com.aptana.core.epl.IMemento;

/**
 * An instance of a server. This extends IRUIMapper so that we can resolve files to URIs and URIs to files based on the
 * document root and base URL.
 * 
 * @author cwilliams
 */
public interface IServer extends IURIMapper
{

	/**
	 * Returns true if this type of configurations should be persistent by manager
	 * 
	 * @return
	 */
	public boolean isPersistent();

	/**
	 * @return
	 */
	public IServerType getType();

	/*
	 * @see com.aptana.core.Identifiable#getId()
	 */
	public String getId();

	/**
	 * @return the name
	 */
	public String getName();

	/**
	 * FIXME Can we avoid making this public?
	 * 
	 * @param memento
	 */
	public void saveState(IMemento memento);

	/**
	 * FIXME Can we avoid making this public?
	 * 
	 * @param memento
	 */
	public void loadState(IMemento memento);

}