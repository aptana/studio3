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
package com.aptana.filesystem.secureftp.internal;

import org.eclipse.osgi.util.NLS;

/* package */ class Messages extends NLS {

	private static final String BUNDLE_NAME = "com.aptana.filesystem.secureftp.internal.messages"; //$NON-NLS-1$

	public static String FTPSConnectionFileManager_Authenticating;
	public static String FTPSConnectionFileManager_Connecting;
	public static String FTPSConnectionFileManager_ConnectionHasBeenInitiated;
	public static String FTPSConnectionFileManager_ConnectionInitializationFailed;
	public static String FTPSConnectionFileManager_ConnectionNotInitialized;
	public static String FTPSConnectionFileManager_EstablishingConnection;
	public static String FTPSConnectionFileManager_FailedAuthenticate;
	public static String FTPSConnectionFileManager_FailedEstablishConnection;
	public static String FTPSConnectionFileManager_FTPSAuthentication;
	public static String FTPSConnectionFileManager_HostNameNotFound;
	public static String FTPSConnectionFileManager_PasswordNotAccepted;
	public static String FTPSConnectionFileManager_RemoteFolderNotFound;
	public static String FTPSConnectionFileManager_ServerSertificateError;

	public static String FTPSConnectionFileManager_SpecifyPassword;
	public static String SFTPConnectionFileManager_Authenticating;
	public static String SFTPConnectionFileManager_ClosingConnection;
	public static String SFTPConnectionFileManager_ConnectionHasBeenInitialized;
	public static String SFTPConnectionFileManager_ConnectionNotInitialized;

	public static String SFTPConnectionFileManager_CreateFile0Failed;
	public static String SFTPConnectionFileManager_ErrorSendKeepAlive;

	public static String SFTPConnectionFileManager_EstablishingConnection;
	public static String SFTPConnectionFileManager_FailedAuthenticate;
	public static String SFTPConnectionFileManager_FailedAuthenticatePublicKey;
	public static String SFTPConnectionFileManager_FailedCreateDirectory;
	public static String SFTPConnectionFileManager_FailedDeleteDirectory;
	public static String SFTPConnectionFileManager_FailedDeleteFile;
	public static String SFTPConnectionFileManager_FailedDeleting;
	public static String SFTPConnectionFileManager_FailedDeletingFile;
	public static String SFTPConnectionFileManager_FailedDisconnectConnection;
	public static String SFTPConnectionFileManager_FailedEstablishConnection;
	public static String SFTPConnectionFileManager_FailedFetchDirectory;
	public static String SFTPConnectionFileManager_FailedFetchFileInfo;
	public static String SFTPConnectionFileManager_FailedInitiatingFile;
	public static String SFTPConnectionFileManager_FailedListDirectory;
	public static String SFTPConnectionFileManager_FailedOpeningFile;
	public static String SFTPConnectionFileManager_FailedRename;
	public static String SFTPConnectionFileManager_FailedRenaming;
	public static String SFTPConnectionFileManager_FailedSetModificationTime;
	public static String SFTPConnectionFileManager_FailedSetPermissions;
	public static String SFTPConnectionFileManager_FailedChangeGroup;
	public static String SFTPConnectionFileManager_GatheringFileDetails;
	public static String SFTPConnectionFileManager_HostNameNotFound;
	public static String SFTPConnectionFileManager_IncorrectLogin;
	public static String SFTPConnectionFileManager_InitializationFailed;
	public static String SFTPConnectionFileManager_InitiatingFileDownload;
	public static String SFTPConnectionFileManager_InvalidGroup;
	public static String SFTPConnectionFileManager_InvalidPassphrase;
	public static String SFTPConnectionFileManager_InvalidPrivateKey;
	public static String SFTPConnectionFileManager_KeyRequirePassphrase;
	public static String SFTPConnectionFileManager_PassphraseNotAccepted;
	public static String SFTPConnectionFileManager_PasswordNotAccepted;

	public static String SFTPConnectionFileManager_PermissionDenied0;
	public static String SFTPConnectionFileManager_PublicKeyAuthentication;
	public static String SFTPConnectionFileManager_RemoteFolderNotFound;
	public static String SFTPConnectionFileManager_SFTPAuthentication;
	public static String SFTPConnectionFileManager_SpecifyPassphrase;
	public static String SFTPConnectionFileManager_SpecifyPassword;
	public static String SFTPConnectionFileManager_UnableToReadPrivateKey;
	public static String SFTPFileDownloadInputStream_ErrorDownload;

	public static String SFTPFileUploadOutputStream_ErrorCloseStream;

	public static String SFTPFileUploadOutputStream_ErrorUpload;

	public static String SSHHostValidator_FailedLoadKnownHosts;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
