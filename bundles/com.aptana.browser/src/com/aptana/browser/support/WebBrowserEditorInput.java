/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.browser.support;

import java.net.URL;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class WebBrowserEditorInput extends org.eclipse.ui.internal.browser.WebBrowserEditorInput {


	/**
	 * @param url
	 */
	public WebBrowserEditorInput(URL url) {
		super(url);
	}

	/**
	 * @param url
	 * @param style
	 */
	public WebBrowserEditorInput(URL url, int style) {
		super(url, style);
	}

	/**
	 * @param url
	 * @param style
	 * @param browserId
	 */
	public WebBrowserEditorInput(URL url, int style, String browserId) {
		super(url, style, browserId);
	}

}
