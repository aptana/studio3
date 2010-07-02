/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io.vfs;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;


/**
 * @author Max Stepanov
 *
 */
public interface IConnectionFileManager {

	
	/**
	 * Retrieves the host name or IP address
	 * @return
	 */
	public String getHost();

	/**
	 * Sets the remote host name or IP address
	 * @param host
	 */
	public void setHost(String host);

	/**
	 * Retrieves the numeric port
	 * @return
	 */
	public int getPort();

	/**
	 * Sets the communication port
	 * @param port
	 */
	public void setPort(int port);

	/**
	 * Retrieves the username or login used while connection
	 * @return
	 */
	public String getLogin();

	/**
	 * Sets the username or login used to connect
	 * @param login
	 */
	public void setLogin(String login);
	
	/**
	 * Gets the password used to connect
	 * @return
	 */
	public char[] getPassword();

	/**
	 * Sets the password used to connect
	 * @param password
	 */
	public void setPassword(char[] password);

	/**
	 * Retrieves the base path of this connection point
	 * @return
	 */
	public IPath getBasePath();

	/**
	 * Sets the base path of this connection point
	 * @param basePath
	 */
	public void setBasePath(IPath basePath);
	
	public void connect(IProgressMonitor monitor) throws CoreException;
	public boolean isConnected();
	public void disconnect(IProgressMonitor monitor) throws CoreException;

	public URI getCanonicalURI(IPath path);

	public IExtendedFileInfo fetchInfo(IPath path, int options, IProgressMonitor monitor) throws CoreException;
	public String[] childNames(IPath path, int options, IProgressMonitor monitor) throws CoreException;
	public IExtendedFileInfo[] childInfos(IPath path, int options, IProgressMonitor monitor) throws CoreException;
	
	public InputStream openInputStream(IPath path, int options, IProgressMonitor monitor) throws CoreException;
	public OutputStream openOutputStream(IPath path, int options, IProgressMonitor monitor) throws CoreException;

	public void delete(IPath path, int options, IProgressMonitor monitor) throws CoreException;
	public void mkdir(IPath path, int options, IProgressMonitor monitor) throws CoreException;
	
	public void putInfo(IPath path, IFileInfo info, int options, IProgressMonitor monitor) throws CoreException;
	
	public void move(IPath sourcePath, IPath destinationPath, int options, IProgressMonitor monitor) throws CoreException;

}
