/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.secureftp.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.filesystem.secureftp.SecureUtils;
import com.enterprisedt.net.ftp.ssh.SSHFTPPublicKey;
import com.enterprisedt.net.ftp.ssh.SSHFTPValidator;

/**
 * @author Max Stepanov
 *
 */
public class SSHHostValidator extends SSHFTPValidator {

	/**
	 * 
	 */
	public SSHHostValidator() {
		String ssh_home = SecureUtils.getSSH_HOME();
		File knownHosts = new File(ssh_home, "known_hosts"); //$NON-NLS-1$
		if (knownHosts.exists() && knownHosts.isFile()) {
			InputStream fin = null;
			try {
				loadKnownHosts(fin = new FileInputStream(knownHosts));
			} catch (Exception e) {
				SecureFTPPlugin.log(new Status(IStatus.WARNING, SecureFTPPlugin.PLUGIN_ID, Messages.SSHHostValidator_FailedLoadKnownHosts, e));
			} finally {
				if (fin != null) {
					try {
						fin.close();
					}
					catch (IOException ignore) {
						ignore.getCause();
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.enterprisedt.net.ftp.ssh.SSHFTPValidator#validate(java.lang.String, com.enterprisedt.net.ftp.ssh.SSHFTPPublicKey, boolean)
	 */
	@Override
	protected boolean validate(String hostSpecifier, SSHFTPPublicKey publicKey, boolean hostKnown) {
		if (!hostKnown) {
			return true;
		}
		return super.validate(hostSpecifier, publicKey, hostKnown);
	}
}
