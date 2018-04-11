/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.secureftp;

import com.aptana.filesystem.ftp.IFTPConstants;


/**
 * @author Max Stepanov
 *
 */
public interface ISFTPConstants extends IFTPConstants {

	public static final int SFTP_PORT_DEFAULT = 22;

	public static final String COMPRESSION_AUTO = "AUTO"; //$NON-NLS-1$
	public static final String COMPRESSION_NONE = "NONE"; //$NON-NLS-1$
	public static final String COMPRESSION_ZLIB = "ZLIB"; //$NON-NLS-1$

}
