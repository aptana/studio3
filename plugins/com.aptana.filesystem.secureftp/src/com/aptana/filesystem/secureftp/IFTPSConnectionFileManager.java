/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.secureftp;

import org.eclipse.core.runtime.IPath;

import com.aptana.core.io.vfs.IConnectionFileManager;

/**
 * @author Max Stepanov
 *
 */
public interface IFTPSConnectionFileManager extends IConnectionFileManager {

	public void init(String host, int port, IPath basePath, String login, char[] password, boolean explicit, boolean passive, String transferType, String encoding, String timezone, boolean validateCertificate, boolean noSSLSessionResumption);
}
