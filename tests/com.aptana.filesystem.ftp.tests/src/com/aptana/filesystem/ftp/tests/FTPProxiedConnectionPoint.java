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

package com.aptana.filesystem.ftp.tests;

import org.eclipse.core.runtime.Platform;

import com.aptana.filesystem.ftp.FTPConnectionPoint;
import com.aptana.filesystem.ftp.IFTPConnectionFileManager;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.vfs.IConnectionFileManager;

/**
 * @author Max Stepanov
 *
 */
public class FTPProxiedConnectionPoint extends FTPConnectionPoint {

	public void setFTPException(boolean value) {
		((FTPProxiedConnectionFileManager)connectionFileManager).setFTPException(value);
	}
	
	public boolean getFTPException() {
		return ((FTPProxiedConnectionFileManager)connectionFileManager).getFTPException();
	}

	public void setIOException(boolean value) {
		((FTPProxiedConnectionFileManager)connectionFileManager).setIOException(value);
	}

	public boolean getIOException() {
		return ((FTPProxiedConnectionFileManager)connectionFileManager).getIOException();
	}

	public void setOperationCanceledException(boolean value) {
		((FTPProxiedConnectionFileManager)connectionFileManager).setOperationCanceledException(value);
	}
	
	public boolean getOperationCanceledException() {
		return ((FTPProxiedConnectionFileManager)connectionFileManager).getOperationCanceledException();
	}

	public void setCoreException(boolean value) {
		((FTPProxiedConnectionFileManager)connectionFileManager).setCoreException(value);
	}
	
	public boolean getCoreException() {
		return ((FTPProxiedConnectionFileManager)connectionFileManager).getCoreException();
	}

	public void setUnknownHostException(boolean value) {
		((FTPProxiedConnectionFileManager)connectionFileManager).setUnknownHostException(value);
	}
	
	public boolean getUnknownHostException() {
		return ((FTPProxiedConnectionFileManager)connectionFileManager).getUnknownHostException();
	}

	public void setFileNotFoundException(boolean value) {
		((FTPProxiedConnectionFileManager)connectionFileManager).setFileNotFoundException(value);
	}
	
	public boolean getFileNotFoundException() {
		return ((FTPProxiedConnectionFileManager)connectionFileManager).getFileNotFoundException();
	}

	public void forceStreamException(boolean value)
	{
		((FTPProxiedConnectionFileManager)connectionFileManager).forceStreamException(value);
	}

	public boolean getStreamException()
	{
		return ((FTPProxiedConnectionFileManager)connectionFileManager).getStreamException();
	}

	/**
	 * Default constructor
	 */
	public FTPProxiedConnectionPoint() {
		super();
	}

    @Override
	protected synchronized IConnectionFileManager getConnectionFileManager() {
		if (connectionFileManager == null) {
			// find contributed first
			connectionFileManager = (IFTPConnectionFileManager) super.getAdapter(IFTPConnectionFileManager.class);
			if (connectionFileManager == null
					&& Platform.getAdapterManager().hasAdapter(this, IFTPConnectionFileManager.class.getName())) {
				connectionFileManager = (IFTPConnectionFileManager) Platform.getAdapterManager().loadAdapter(this, IFTPConnectionFileManager.class.getName());
			}
			if (connectionFileManager == null) {
				connectionFileManager = new FTPProxiedConnectionFileManager();
			}
			ConnectionContext context = CoreIOPlugin.getConnectionContext(this);
			if (context != null) {
				CoreIOPlugin.setConnectionContext(connectionFileManager, context);
			}
			connectionFileManager.init(host, port, path, login, password, passiveMode, transferType, encoding, timezone);
		}
		return connectionFileManager;
	}
}
