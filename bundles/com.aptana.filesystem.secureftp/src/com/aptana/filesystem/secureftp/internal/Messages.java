/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
