/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.ftp.internal;

import java.net.URI;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.io.vfs.BaseConnectionFileManager;
import com.aptana.core.io.vfs.ExtendedFileInfo;
import com.aptana.core.util.URLEncoder;
import com.aptana.filesystem.ftp.FTPPlugin;
import com.aptana.filesystem.ftp.Policy;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;

/**
 * @author Max Stepanov
 */
public abstract class BaseFTPConnectionFileManager extends BaseConnectionFileManager {

	protected static final int TIMEOUT = 20000;
	protected static final int RETRY = 3;
	protected static final int RETRY_DELAY = 5000;
	protected static final int KEEPALIVE_INTERVAL = 15000;
	protected static final int TRANSFER_BUFFER_SIZE = 32768;
	protected static final int CHECK_CONNECTION_TIMEOUT = 30000;
	protected static final String TMP_UPLOAD_SUFFIX = "._tmp_upload"; //$NON-NLS-1$
	protected static final Pattern PASS_COMMAND_PATTERN = Pattern.compile("^(.*PASS ).+$"); //$NON-NLS-1$

	protected String host;
	protected int port;

	private long lastOperationTime;
	protected String defaultOwner;
	protected String defaultGroup;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.BaseConnectionFileManager#canUseTemporaryFile(org.eclipse.core.runtime.IPath,
	 * com.aptana.core.io.vfs.ExtendedFileInfo, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected boolean canUseTemporaryFile(IPath path, ExtendedFileInfo fileInfo, IProgressMonitor monitor) {
		ConnectionContext context = CoreIOPlugin.getConnectionContext(this);
		if (context != null && context.containsKey(ConnectionContext.USE_TEMPORARY_ON_UPLOAD)) {
			return context.getBoolean(ConnectionContext.USE_TEMPORARY_ON_UPLOAD);
		}
		if (fileInfo.exists()) {
			// test if using temporary file for existing file may cause any differences on remote side
			if (defaultOwner == null || defaultGroup == null) {
				IPath tempFile = basePath.append(path).removeLastSegments(1)
						.append(System.currentTimeMillis() + TMP_UPLOAD_SUFFIX + ".txt"); //$NON-NLS-1$
				ExtendedFileInfo tempFileInfo = null;
				monitor.beginTask(Messages.BaseFTPConnectionFileManager_GetheringServerDetails, 3);
				try {
					try {
						createFile(tempFile, Policy.subMonitorFor(monitor, 1));
						tempFileInfo = fetchFileInternal(tempFile, EFS.NONE, Policy.subMonitorFor(monitor, 1));
					} finally {
						deleteFile(tempFile, Policy.subMonitorFor(monitor, 1));
					}
				} catch (Exception e) {
					FTPPlugin.log(new Status(IStatus.WARNING, FTPPlugin.PLUGIN_ID,
							Messages.BaseFTPConnectionFileManager_ErrorDetectOwnerGroup, e));
				}

				if (tempFileInfo != null) {
					defaultOwner = tempFileInfo.getOwner();
					defaultGroup = tempFileInfo.getGroup();
				}
			}
			if (defaultOwner == null) {
				defaultOwner = Long.toHexString(System.currentTimeMillis());
			}
			if (defaultGroup == null) {
				defaultGroup = Long.toHexString(System.currentTimeMillis());
			}
			if (!defaultOwner.equals(fileInfo.getOwner()) || !defaultGroup.equals(fileInfo.getGroup())) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#getCanonicalURI(org.eclipse.core.runtime.IPath)
	 */
	public URI getCanonicalURI(IPath path) {
		// TODO:max - trace links here
		return getRootCanonicalURI().resolve(URLEncoder.encode(basePath.append(path).toPortableString(), null, null));
	}

	protected abstract void checkConnected() throws Exception; // $codepro.audit.disable declaredExceptions

	protected abstract URI getRootCanonicalURI();

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.BaseConnectionFileManager#testConnection(boolean)
	 */
	@Override
	protected void testConnection(boolean force) {
		if (!isConnected()) {
			return;
		}
		if (force || (System.currentTimeMillis() - lastOperationTime > CHECK_CONNECTION_TIMEOUT)) {
			try {
				checkConnected();
				if (isConnected()) {
					setLastOperationTime();
				}
			} catch (Exception e) {
				FTPPlugin.log(new Status(IStatus.WARNING, FTPPlugin.PLUGIN_ID,
						Messages.BaseFTPConnectionFileManager_connection_check_failed, e));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.BaseConnectionFileManager#setLastOperationTime()
	 */
	@Override
	protected void setLastOperationTime() {
		lastOperationTime = System.currentTimeMillis();
	}
}
