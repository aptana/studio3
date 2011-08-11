/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old;

/**
 * @author Kevin Lindsey
 */
public interface SyncState
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
