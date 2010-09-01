/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.browser;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.aptana.swt.webkitbrowser.WebKitBrowser;

/**
 * @author Max Stepanov
 *
 */
public class WebBrowserViewer extends Composite {

	public static final int NAVIGATION_BAR = 1 << 0;
	
	private WebKitBrowser browser;
	
	/**
	 * @param parent
	 * @param style
	 */
	public WebBrowserViewer(Composite parent, int style) {
		super(parent, SWT.NONE);
		setLayout(GridLayoutFactory.fillDefaults().create());
		if ((style & NAVIGATION_BAR) != 0) {
			Composite container = new Composite(this, SWT.NONE);
			container.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
			container.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
		}
		browser = new WebKitBrowser(this, SWT.NONE);
		browser.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Composite#setFocus()
	 */
	@Override
	public boolean setFocus() {
		return browser.setFocus();
	}
	
	public Control getBrowserControl() {
		return browser;
	}

	/**
	 * @param html
	 * @return
	 * @see com.aptana.swt.webkitbrowser.WebKitBrowser#setText(java.lang.String)
	 */
	public boolean setText(String html) {
		return browser.setText(html);
	}

	/**
	 * @param url
	 * @param postData
	 * @param headers
	 * @return
	 * @see com.aptana.swt.webkitbrowser.WebKitBrowser#setUrl(java.lang.String, java.lang.String, java.lang.String[])
	 */
	public boolean setUrl(String url, String postData, String[] headers) {
		return browser.setUrl(url, postData, headers);
	}

	/**
	 * @param url
	 * @return
	 * @see com.aptana.swt.webkitbrowser.WebKitBrowser#setUrl(java.lang.String)
	 */
	public boolean setUrl(String url) {
		return browser.setUrl(url);
	}

}
