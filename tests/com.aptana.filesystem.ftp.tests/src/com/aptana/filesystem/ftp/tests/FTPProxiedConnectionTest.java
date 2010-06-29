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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.OperationCanceledException;

import com.aptana.core.io.tests.BaseConnectionTest;
import com.aptana.filesystem.ftp.FTPConnectionPoint;

/**
 * @author Max Stepanov
 */
public class FTPProxiedConnectionTest extends BaseConnectionTest
{	
	@Override
	protected void setUp() throws Exception
	{
		FTPConnectionPoint ftpcp = new FTPProxiedConnectionPoint();
		ftpcp.setHost(getConfig().getProperty("ftp.host", "10.10.1.60")); //$NON-NLS-1$ //$NON-NLS-2$
		ftpcp.setLogin(getConfig().getProperty("ftp.username", "ftpuser")); //$NON-NLS-1$ //$NON-NLS-2$
		ftpcp.setPassword(getConfig().getProperty("ftp.password",	//$NON-NLS-1$
				String.valueOf(new char[] { 'l', 'e', 't', 'm', 'e', 'i', 'n'})).toCharArray());
		cp = ftpcp;
		super.setUp();
	}

	public final void testConnectDisconnectException() throws CoreException
	{
		cp.connect(null);
		assertTrue(cp.isConnected());
		assertTrue(cp.canDisconnect());
		cp.disconnect(null);
		assertFalse(cp.isConnected());
		assertFalse(cp.canDisconnect());

		FTPConnectionPoint ftpcp = (FTPConnectionPoint)cp;
		
		// set host to non-existent version
		String oldHost = ftpcp.getHost();
		try {
			ftpcp.setHost(null);
			ftpcp.connect(null);
			fail();
		}
		catch(CoreException e) {
			
		}
		cp.disconnect(null);
		ftpcp.setHost(oldHost);
		
		// set port to non-existent version
		int oldPort = ftpcp.getPort();
		try {
			ftpcp.setPort(0);
			ftpcp.connect(null);
			fail();
		}
		catch(CoreException e) {
			
		}
		cp.disconnect(null);
		ftpcp.setPort(oldPort);

		// set username to null
		String username = ftpcp.getLogin();
		try {
			ftpcp.setLogin(null);
			ftpcp.connect(null);
			fail();
		}
		catch(OperationCanceledException e) {
			
		}
		cp.disconnect(null);
		ftpcp.setLogin(username);

		char[] pass = ftpcp.getPassword();

		// null password means it will try and get a saved value
		ftpcp.setPassword(null);
		ftpcp.connect(null);
		assertTrue(cp.isConnected());

		try {
			ftpcp.setPassword(new char[] {'a'});
			ftpcp.connect(null);
			fail();
		}
		catch(OperationCanceledException e) {
			
		}
		cp.disconnect(null);
		ftpcp.setPassword(pass);
	
	}
	
	public final void testIncorrectPaths() throws CoreException
	{
		FTPConnectionPoint ftpcp = (FTPConnectionPoint)cp;
		IPath basePath = ftpcp.getPath();
		
		ftpcp.setPath(null);
		ftpcp.connect(null);
		
		ftpcp.setPath(basePath);
	}
}
