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
package com.aptana.ide.syncing.core.old;

/**
 * @author Kevin Lindsey
 */
public final class SyncState
{
	/**
	 * Unknown state
	 */
	public static final int Unknown = 0;

	/**
	 * Ignore this sync item when performing actions on a sync item list
	 */
	public static final int Ignore = 1;

	/**
	 * This sync item exists on both the client and the server and they match
	 */
	public static final int ItemsMatch = 2;

	/**
	 * The sync item exists on both the client and the server, their modification times match, but their CRC's do no
	 * match
	 */
	public static final int CRCMismatch = 3;

	/**
	 * This sync item exists on both the client and the server, but the client version is newer than the server version
	 */
	public static final int ClientItemIsNewer = 4;

	/**
	 * This sync item exists on both the client and the server, but the server version is newer than the client version
	 */
	public static final int ServerItemIsNewer = 5;

	/**
	 * This sync item only exists on the client
	 */
	public static final int ClientItemOnly = 6;

	/**
	 * This sync item only exists on the server
	 */
	public static final int ServerItemOnly = 7;

	/**
	 * The client file and server file are of different types. In other words, one file is a directory and the other is
	 * not a directory
	 */
	public static final int IncompatibleFileTypes = 8;

	/**
	 * ClientItemDeleted
	 */
	public static final int ClientItemDeleted = 9;

	/**
	 * ServerItemDeleted
	 */
	public static final int ServerItemDeleted = 10;
}
