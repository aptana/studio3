/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old;

import org.eclipse.osgi.util.NLS;

/**
 * Messages
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.syncing.core.old.messages"; //$NON-NLS-1$

	/**
	 * Synchronizer_BeginningDownload
	 */
	public static String Synchronizer_BeginningDownload;

	/**
	 * Synchronizer_BeginningFullSync
	 */
	public static String Synchronizer_BeginningFullSync;

	/**
	 * Synchronizer_BeginningUpload
	 */
	public static String Synchronizer_BeginningUpload;

	/**
	 * Synchronizer_ClientFileManagerCannotBeNull
	 */
	public static String Synchronizer_ClientFileManagerCannotBeNull;

	public static String Synchronizer_Comparing_Files;

	public static String Synchronizer_Completed;

	/**
	 * Synchronizer_CreatedDirectory
	 */
	public static String Synchronizer_CreatedDirectory;

	public static String Synchronizer_Destination_Newer;

	public static String Synchronizer_Directory;

	/**
	 * Synchronizer_Downloading
	 */
	public static String Synchronizer_Downloading;

	public static String Synchronizer_Downloading_Files;

	public static String Synchronizer_ERR_RootNotExist;

	/**
	 * Synchronizer_Error
	 */
	public static String Synchronizer_Error;

	/**
	 * Synchronizer_Error_Extended
	 */
	public static String Synchronizer_Error_Extended;

	/**
	 * Synchronizer_ErrorClosingStreams
	 */
	public static String Synchronizer_ErrorClosingStreams;

	/**
	 * Synchronizer_ErrorDuringSync
	 */
	public static String Synchronizer_ErrorDuringSync;

	/**
	 * Synchronizer_ErrorRetrievingCRC
	 */
	public static String Synchronizer_ErrorRetrievingCRC;

	/**
	 * Synchronizer_FileNotContained
	 */
	public static String Synchronizer_FileNotContained;

	/**
	 * Synchronizer_FullSyncCRCMismatches
	 */
	public static String Synchronizer_FullSyncCRCMismatches;

	public static String Synchronizer_Gathering_Destination;

	public static String Synchronizer_Gathering_Source;

	public static String Synchronizer_Generating_Comparison;

	public static String Synchronizer_Incompatible_Types;

	public static String Synchronizer_Item_Not_On_Destination;

	public static String Synchronizer_Items_Identical;

	public static String Synchronizer_Listing_Complete;

	/**
	 * Synchronizer_ServerFileManagerCannotBeNull
	 */
	public static String Synchronizer_ServerFileManagerCannotBeNull;

	public static String Synchronizer_Skipping_File;

	public static String Synchronizer_Source_Newer;

	/**
	 * Synchronizer_Success
	 */
	public static String Synchronizer_Success;

	public static String Synchronizer_Synchronizing;

	public static String Synchronizer_Times_Modified;

	/**
	 * Synchronizer_Uploading
	 */
	public static String Synchronizer_Uploading;

	public static String Synchronizer_Uploading_Files;

	public static String VirtualFileSyncPair_DestFileInfoErrror;

	public static String VirtualFileSyncPair_SourceFileInfoError;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
