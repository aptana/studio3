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

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
