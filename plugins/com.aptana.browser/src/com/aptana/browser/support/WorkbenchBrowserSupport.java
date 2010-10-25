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

import java.util.HashMap;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.DefaultBrowserSupport;
import org.eclipse.ui.internal.browser.InternalBrowserInstance;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class WorkbenchBrowserSupport extends DefaultBrowserSupport {

	private static final String DEFAULT_ID_BASE = "com.aptana.browser.defaultBrowser"; //$NON-NLS-1$

	/**
	 * 
	 */
	public WorkbenchBrowserSupport() {
		super();
		instance = this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.browser.IWorkbenchBrowserSupport#createBrowser(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public IWebBrowser createBrowser(int style, String browserId, String name, String tooltip) throws PartInitException {
		if (browserId == null) {
			browserId = getDefaultId();
		}
		if (getExistingWebBrowser(browserId) instanceof InternalBrowserInstance) {
			return super.createBrowser(style, browserId, name, tooltip);
		}
		if ((style & AS_EXTERNAL) != 0) {
			return super.createBrowser(style, browserId, name, tooltip);
		}
		IWebBrowser webBrowser = null;
		if ((style & IWorkbenchBrowserSupport.AS_VIEW) != 0) {
			webBrowser = new BrowserViewInstance(browserId, style, name, tooltip);
		} else {
			webBrowser = new BrowserEditorInstance(browserId, style, name, tooltip);
		}
		// we should only share internal browsers within one workbench window. Each workbench window can have a shared browser with the same id
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		Integer key = Integer.valueOf(workbenchWindow.hashCode());
		HashMap wmap = (HashMap) browserIdMap.get(browserId);
		if (wmap == null) {
			wmap = new HashMap();
			browserIdMap.put(browserId, wmap);
		}
		wmap.put(key, webBrowser);
		return webBrowser;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.browser.AbstractWorkbenchBrowserSupport#isInternalWebBrowserAvailable()
	 */
	@Override
	public boolean isInternalWebBrowserAvailable() {
		return true;
	}

	private String getDefaultId() {
		String id = null;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			id = DEFAULT_ID_BASE + i;
			if (browserIdMap.get(id) == null)
				break;
		}
		return id;
	}

}
