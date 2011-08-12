/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.model.xhr;

import java.util.Date;

/**
 * @author Max Stepanov
 */
public interface IXHRTransfer {

	public interface IHeader {
		/**
		 * getName
		 * 
		 * @return String
		 */
		String getName();

		/**
		 * getValue
		 * 
		 * @return String
		 */
		String getValue();
	}

	/**
	 * URL
	 * 
	 * @return String
	 */
	String getURL();

	/**
	 * Method
	 * 
	 * @return String
	 */
	String getMethod();

	/**
	 * Request date/time
	 * 
	 * @return Date
	 */
	Date getRequestDate();

	/**
	 * Returns request headers
	 * 
	 * @return IHeader[]
	 */
	IHeader[] getRequestHeaders();

	/**
	 * Request body
	 * 
	 * @return String
	 */
	String getRequestBody();

	/**
	 * Response date/time
	 * 
	 * @return Date
	 */
	Date getResponseDate();

	/**
	 * Returns response headers
	 * 
	 * @return IHeader[]
	 */
	IHeader[] getResponseHeaders();

	/**
	 * Response body
	 * 
	 * @return String
	 */
	String getResponseBody();

	/**
	 * Returns true if this request contained authentication
	 * 
	 * @return boolean
	 */
	boolean isAuthenticated();

	/**
	 * Returns response status code
	 * 
	 * @return int
	 */
	int getStatusCode();

	/**
	 * Returns response status text
	 * 
	 * @return String
	 */
	String getStatusText();

	/**
	 * Returns true if the response contains error
	 * 
	 * @return boolean
	 */
	boolean hasError();

}
