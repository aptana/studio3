/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.secureftp;

import com.aptana.filesystem.ftp.IBaseFTPConnectionPoint;

/**
 * @author Max Stepanov
 *
 */
public interface IFTPSConnectionPoint extends IBaseFTPConnectionPoint {

	public static final String TYPE_FTPS = "ftps"; //$NON-NLS-1$

	/**
	 * @return explicit
	 */
	public boolean isExplicit();

	/**
	 * @param explicit the explicit to set
	 */
	public void setExplicit(boolean explicit);
	
	/**
	 * 
	 * @return
	 */
	public boolean isValidateCertificate();
	
	/**
	 * 
	 * @param validate
	 */
	public void setValidateCertificate(boolean validate);

	/**
	 * @return the noSSLSessionResumption
	 */
	public boolean isNoSSLSessionResumption();

	/**
	 * @param noSSLSessionResumption the noSSLSessionResumption to set
	 */
	public void setNoSSLSessionResumption(boolean noSSLSessionResumption);

}
