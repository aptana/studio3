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

package com.aptana.webserver.core.builtin;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.protocol.EventListener;

/**
 * @author Max Stepanov
 *
 */
/* package */ class LocalWebServerLogger implements EventListener {

	/* (non-Javadoc)
	 * @see org.apache.http.nio.protocol.EventListener#fatalIOException(java.io.IOException, org.apache.http.nio.NHttpConnection)
	 */
	public void fatalIOException(IOException ex, NHttpConnection conn) {
		System.out.println("fatalIOException "+ex.getMessage()); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.apache.http.nio.protocol.EventListener#fatalProtocolException(org.apache.http.HttpException, org.apache.http.nio.NHttpConnection)
	 */
	public void fatalProtocolException(HttpException ex, NHttpConnection conn) {
		System.out.println("fatalProtocolException "+ex.getMessage()); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.apache.http.nio.protocol.EventListener#connectionOpen(org.apache.http.nio.NHttpConnection)
	 */
	public void connectionOpen(NHttpConnection conn) {
		System.out.println("connectionOpen"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.apache.http.nio.protocol.EventListener#connectionClosed(org.apache.http.nio.NHttpConnection)
	 */
	public void connectionClosed(NHttpConnection conn) {
		System.out.println("connectionClosed"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.apache.http.nio.protocol.EventListener#connectionTimeout(org.apache.http.nio.NHttpConnection)
	 */
	public void connectionTimeout(NHttpConnection conn) {
		System.out.println("connectionTimeout"); //$NON-NLS-1$
	}

}
