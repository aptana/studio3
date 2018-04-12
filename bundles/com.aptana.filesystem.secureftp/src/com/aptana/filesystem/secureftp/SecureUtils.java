/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.secureftp;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jsch.internal.core.IConstants;
import org.eclipse.jsch.internal.core.JSchCorePlugin;
import org.eclipse.jsch.internal.core.PreferenceInitializer;

import com.aptana.filesystem.secureftp.internal.SecureFTPPlugin;
import com.enterprisedt.net.j2ssh.transport.publickey.InvalidSshKeyException;
import com.enterprisedt.net.j2ssh.transport.publickey.SshPrivateKeyFile;
import com.enterprisedt.net.puretls.LoadProviders;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public final class SecureUtils {

	private static final String[] EMPTY = new String[0];

	/**
	 * 
	 */
	private SecureUtils() {
	}

	public static boolean isKeyPassphraseProtected(File keyFile) throws CoreException {
		try {
			LoadProviders.init();
			SshPrivateKeyFile privateKeyFile = SshPrivateKeyFile.parse(keyFile);
			return privateKeyFile.isPassphraseProtected();
		} catch (InvalidSshKeyException e) {
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID, MessageFormat.format(Messages.SecureUtils_InvalidPrivateKey, keyFile.getAbsolutePath()), e));
		} catch (IOException e) {
			throw new CoreException(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID, MessageFormat.format(Messages.SecureUtils_UnableToReadPrivateKey, keyFile.getAbsolutePath())));
		}
	}
	
	public static boolean isPassphraseValid(File keyFile, char[] password) {
		try {
			SshPrivateKeyFile.parse(keyFile).toPrivateKey(String.copyValueOf(password));
			return true;
		} catch (InvalidSshKeyException e) {
			if (e.getCause() instanceof NoSuchAlgorithmException) {
				SecureFTPPlugin.log(new Status(IStatus.WARNING, SecureFTPPlugin.PLUGIN_ID, e.getCause().getMessage()));
			}
		} catch (IOException ignore) {
			ignore.getCause();
		}
		return false;
	}
	
	public static String getSSH_HOME() {
		return Platform.getPreferencesService().getString(JSchCorePlugin.ID, IConstants.KEY_SSH2HOME, PreferenceInitializer.SSH_HOME_DEFAULT, null);
	}

	public static String[] getPrivateKeys() {
		String value = Platform.getPreferencesService().getString(JSchCorePlugin.ID, IConstants.KEY_PRIVATEKEY, IConstants.PRIVATE_KEYS_DEFAULT, null);
		if (value != null && value.length() > 0) {
			return value.trim().split(","); //$NON-NLS-1$
		}
		return EMPTY;
	}
}
