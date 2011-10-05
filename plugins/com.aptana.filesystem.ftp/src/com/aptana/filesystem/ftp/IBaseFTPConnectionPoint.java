/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.ftp;

import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;

/**
 * @author Max Stepanov
 */
public interface IBaseFTPConnectionPoint extends IBaseRemoteConnectionPoint {

	public static final String TYPE_FTP = "ftp"; //$NON-NLS-1$

	/**
	 * @return the passiveMode
	 */
	public boolean isPassiveMode();

	/**
	 * @param passiveMode
	 *            the passiveMode to set
	 */
	public void setPassiveMode(boolean passiveMode);

	/**
	 * @return the encoding
	 */
	public String getEncoding();

	/**
	 * @param encoding
	 *            the encoding to set
	 */
	public void setEncoding(String encoding);

	/**
	 * @return the timezone
	 */
	public String getTimezone();

	/**
	 * @param timezone
	 *            the timezone to set
	 */
	public void setTimezone(String timezone);

}
