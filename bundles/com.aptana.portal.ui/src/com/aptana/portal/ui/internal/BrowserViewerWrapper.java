/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.portal.ui.internal;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.browser.BrowserViewer;

import com.aptana.browser.WebBrowserViewer;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class BrowserViewerWrapper {

	private final Object browserViewer;
	
	public static BrowserViewerWrapper createSWTBrowserViewer(Composite parent, int style) {
		return new BrowserViewerWrapper(new BrowserViewer(parent, style));
	}

	public static BrowserViewerWrapper createWebkitBrowserViewer(Composite parent, int style) {
		return new BrowserViewerWrapper(new WebBrowserViewer(parent, style));
	}

	/**
	 * 
	 */
	private BrowserViewerWrapper(Object browserViewer) {
		this.browserViewer = browserViewer;
	}
	/**
	 * @return
	 * @see org.eclipse.ui.internal.browser.BrowserViewer#getBrowser()
	 */
	public Object getBrowser() {
		if (browserViewer instanceof WebBrowserViewer) {
			return ((WebBrowserViewer) browserViewer).getBrowser();
		} else {
			return ((BrowserViewer) browserViewer).getBrowser();
		}
	}

}
