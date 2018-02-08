/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.secureftp.internal;

import com.enterprisedt.net.ftp.ssl.SSLFTPStandardValidator;

/**
 * @author Max Stepanov
 *
 */
public class SSLHostValidator extends SSLFTPStandardValidator {

	/* (non-Javadoc)
	 * @see com.enterprisedt.net.ftp.ssl.SSLFTPStandardValidator#checkCommonName(java.lang.String, java.lang.String)
	 */
	@Override
	protected boolean checkCommonName(String certCommonName, String serverHostName) {
		if (super.checkCommonName(certCommonName, serverHostName)) {
			return true;
		}
		if (certCommonName.startsWith("*.")) { //$NON-NLS-1$
			return serverHostName.toLowerCase().endsWith(certCommonName.substring(1).toLowerCase());
		}
		return false;
	}
}
