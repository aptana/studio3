/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import java.util.Collection;
import java.util.List;

import com.aptana.core.util.IBrowserUtil.BrowserInfo;

public interface IBrowserProvider
{

	/**
	 * @return a list with the current information on configured web browsers.
	 * @note the default browser may have a null location.
	 */
	List<BrowserInfo> getWebBrowsers();

	/**
	 * @return the new browsers that have been found.
	 * @note the default browser may have a null location.
	 */
	Collection<BrowserInfo> searchMoreBrowsers();

	/**
	 * @return information on the currently configured web browser.
	 * @note the default browser may have a null location.
	 */
	BrowserInfo getCurrentWebBrowser();

	/**
	 * @return the version of the given browser.
	 */
	String getBrowserVersion(BrowserInfo info);
}
