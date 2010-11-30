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

package com.aptana.filesystem.ftp.internal;

import java.net.URI;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.util.URLEncoder;
import com.aptana.filesystem.ftp.FTPPlugin;
import com.aptana.filesystem.ftp.Policy;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.vfs.BaseConnectionFileManager;
import com.aptana.ide.core.io.vfs.ExtendedFileInfo;

/**
 * @author Max Stepanov
 *
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
	
	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.vfs.BaseConnectionFileManager#canUseTemporaryFile(org.eclipse.core.runtime.IPath, com.aptana.ide.core.io.vfs.ExtendedFileInfo, org.eclipse.core.runtime.IProgressMonitor)
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
				IPath tempFile = basePath.append(path).removeLastSegments(1).append(System.currentTimeMillis()+TMP_UPLOAD_SUFFIX);
				ExtendedFileInfo tempFileInfo = null;
				monitor.beginTask(Messages.BaseFTPConnectionFileManager_GetheringServerDetails, 3);
				try {
					try {
						createFile(tempFile, Policy.subMonitorFor(monitor, 1));
						tempFileInfo = fetchFile(tempFile, EFS.NONE, Policy.subMonitorFor(monitor, 1));
					} finally {
						deleteFile(tempFile, Policy.subMonitorFor(monitor, 1));
					}
				} catch (Exception e) {
					FTPPlugin.log(new Status(IStatus.WARNING, FTPPlugin.PLUGIN_ID, Messages.BaseFTPConnectionFileManager_ErrorDetectOwnerGroup, e));
				}

				if (tempFileInfo != null) {
					defaultOwner = tempFileInfo.getOwner();
					defaultGroup = tempFileInfo.getGroup();
				} else {
					return false;
				}
			}
			if (!defaultOwner.equals(fileInfo.getOwner())
				|| !defaultGroup.equals(fileInfo.getGroup())) {
				return false;
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.vfs.IConnectionFileManager#getCanonicalURI(org.eclipse.core.runtime.IPath)
	 */
	public URI getCanonicalURI(IPath path) {
		// TODO:max - trace links here
		return getRootCanonicalURI().resolve(URLEncoder.encode(basePath.append(path).toPortableString(), null, null));
	}

	protected abstract void checkConnected() throws Exception;
	protected abstract URI getRootCanonicalURI();
			
	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.vfs.BaseConnectionFileManager#testConnection()
	 */
	@Override
	protected void testConnection() {
		if (!isConnected()) {
			return;
		}
		if (System.currentTimeMillis() - lastOperationTime > CHECK_CONNECTION_TIMEOUT) {
			try {
				checkConnected();
			} catch (Exception e) {
				FTPPlugin.log(new Status(IStatus.WARNING, FTPPlugin.PLUGIN_ID, Messages.BaseFTPConnectionFileManager_connection_check_failed, e));
			}
		}
		if (isConnected()) {
			setLastOperationTime();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.vfs.BaseConnectionFileManager#setLastOperationTime()
	 */
	@Override
	protected void setLastOperationTime() {
		lastOperationTime = System.currentTimeMillis();		
	}	
}
