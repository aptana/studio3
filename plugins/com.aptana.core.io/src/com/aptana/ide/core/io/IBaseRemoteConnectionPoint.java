/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io;

import org.eclipse.core.runtime.IPath;

/**
 * @author Max Stepanov
 */
public interface IBaseRemoteConnectionPoint extends IConnectionPoint
{

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name);

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id);

	/**
	 * @return the host
	 */
	public String getHost();

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host);

	/**
	 * @return the port
	 */
	public int getPort();

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port);

	/**
	 * @return the path
	 */
	public IPath getPath();

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(IPath path);

	/**
	 * @return the login
	 */
	public String getLogin();

	/**
	 * @param login
	 *            the login to set
	 */
	public void setLogin(String login);

	/**
	 * @return the password
	 */
	public char[] getPassword();

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(char[] password);

}
