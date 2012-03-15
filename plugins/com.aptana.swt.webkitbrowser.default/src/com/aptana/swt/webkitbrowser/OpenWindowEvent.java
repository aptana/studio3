/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.swt.webkitbrowser;

import org.eclipse.swt.browser.WindowEvent;

/**
 * @author Max Stepanov
 *
 */
public class OpenWindowEvent extends WindowEvent {

	private static final long serialVersionUID = 1L;

	public WebKitBrowser browser;
	public String location;
	
	/**
	 * @param widget
	 */
	public OpenWindowEvent(WebKitBrowser browser) {
		super(browser);
	}

}
