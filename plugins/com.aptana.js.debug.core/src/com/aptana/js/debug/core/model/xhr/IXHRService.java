/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.model.xhr;

/**
 * @author Max Stepanov
 */
public interface IXHRService {

	/**
	 * Returns the list of XHT transfers
	 * 
	 * @return IXHRTransfer[]
	 */
	IXHRTransfer[] getTransfers();

	/**
	 * Returns the number of XHR transfers occurred
	 * 
	 * @return int
	 */
	int getTransfersCount();
}
