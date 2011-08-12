/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.ftp.internal;

import org.eclipse.core.runtime.Platform;

import com.aptana.core.util.KeepAliveObjectPool;
import com.aptana.filesystem.ftp.FTPPlugin;
import com.aptana.filesystem.ftp.preferences.FTPPreferenceInitializer;
import com.aptana.filesystem.ftp.preferences.IFTPPreferenceConstants;
import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPClientInterface;
import com.enterprisedt.net.ftp.FTPTransferType;

public final class FTPClientPool extends KeepAliveObjectPool<FTPClientInterface> {

	private IPoolConnectionManager manager;

	public FTPClientPool(IPoolConnectionManager manager) {
		super(Platform.getPreferencesService().getInt(FTPPlugin.PLUGIN_ID, IFTPPreferenceConstants.KEEP_ALIVE_TIME,
				FTPPreferenceInitializer.DEFAULT_KEEP_ALIVE_MINUTES, null) * 60 * 1000);
		this.manager = manager;
		start();
	}

	public FTPClientInterface create() {
		return manager.newClient();
	}

	public void expire(FTPClientInterface ftpClient) {
		if (ftpClient == null) {
			return;
		}
		try {
			ftpClient.quit();
		} catch (Exception e) {
			try {
				ftpClient.quitImmediately();
			} catch (Exception ignore) {
				ignore.getCause();
			}
		}
	}

	public boolean validate(FTPClientInterface o) {
		if (!o.connected()) {
			return false;
		}
		if (o instanceof FTPClient) {
			try {
				((FTPClient) o).noOperation();
				((FTPClient) o).setType(FTPTransferType.BINARY);
			} catch (Exception e) {
				// ignore
				return false;
			}
		}
		return true;
	}
}
