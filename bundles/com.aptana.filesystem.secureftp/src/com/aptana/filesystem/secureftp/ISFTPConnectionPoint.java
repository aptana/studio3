/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.secureftp;

import org.eclipse.core.runtime.IPath;

import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;


/**
 * @author Max Stepanov
 *
 */
public interface ISFTPConnectionPoint extends IBaseRemoteConnectionPoint {

	public static final String TYPE_SFTP = "sftp"; //$NON-NLS-1$

	/**
	 * @return the encoding
	 */
	public String getEncoding();

	/**
	 * @param encoding the encoding to set
	 */
	public void setEncoding(String encoding);

	/**
	 * @return the compression
	 */
	public String getCompression();

	/**
	 * @param compression the compression to set
	 */
	public void setCompression(String compression);
	
	/**
	 * @return the key file path
	 */
	public IPath getKeyFilePath();

	/**
	 * @param keyFilePath the key file path to set
	 */
	public void setKeyFilePath(IPath keyFilePath);

}
