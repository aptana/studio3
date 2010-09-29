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

package com.aptana.browser.support;

import java.net.URL;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.browser.InternalBrowserInstance;
import org.eclipse.ui.internal.browser.WebBrowserUIPlugin;

import com.aptana.browser.parts.WebBrowserView;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class BrowserViewInstance extends InternalBrowserInstance {

	/**
	 * @param id
	 * @param style
	 * @param name
	 * @param tooltip
	 */
	public BrowserViewInstance(String id, int style, String name, String tooltip) {
		super(id, style, name, tooltip);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.browser.IWebBrowser#openURL(java.net.URL)
	 */
	public void openURL(URL url) throws PartInitException {
		WebBrowserView view = (WebBrowserView) part;
		IWorkbenchWindow workbenchWindow = WebBrowserUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = null;
		if (workbenchWindow != null) {
			workbenchPage = workbenchWindow.getActivePage();
		}
		if (workbenchPage == null) {
			throw new PartInitException("Cannot get Workbench page");
		}
		if (view == null) {
			view = (WebBrowserView) workbenchPage.showView(WebBrowserView.VIEW_ID, getId(), IWorkbenchPage.VIEW_CREATE);
			hookPart(workbenchPage, view);
		}
		if (view != null) {
			workbenchPage.bringToTop(view);
			view.setURL(url != null ? url.toExternalForm() : null);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.browser.AbstractWebBrowser#close()
	 */
	@Override
	public boolean close() {
		return ((WebBrowserView) part).close();
	}

}
