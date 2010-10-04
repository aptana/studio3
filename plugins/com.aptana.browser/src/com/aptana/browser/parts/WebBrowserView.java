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

package com.aptana.browser.parts;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.aptana.browser.WebBrowserViewer;
import com.aptana.swt.webkitbrowser.WebKitBrowser;

/**
 * @author Max Stepanov
 *
 */
public class WebBrowserView extends ViewPart {

	public static final String VIEW_ID = "com.aptana.browser.views.webbrowser"; //$NON-NLS-1$

	private WebBrowserViewer browserViewer;
	private int progressWorked;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		browserViewer = new WebBrowserViewer(parent, WebBrowserViewer.NAVIGATION_BAR);
		WebKitBrowser browser = (WebKitBrowser) browserViewer.getBrowser();
		browser.addProgressListener(new ProgressListener() {
			public void changed(ProgressEvent event) {
				if (event.total == 0) {
					return;
				}
				if (event.current == 0) {
					IProgressMonitor progressMonitor = getStatusBarProgressMonitor();
					progressMonitor.done();
					progressMonitor.beginTask("", event.total);
					progressWorked = 0;
				}
				if (progressWorked < event.current) {
					getStatusBarProgressMonitor().worked(event.current-progressWorked);
					progressWorked = event.current;
				}
			}
			
			public void completed(ProgressEvent event) {
				getStatusBarProgressMonitor().done();
			}
		});
		browser.addTitleListener(new TitleListener() {
			public void changed(TitleEvent event) {
				setTitleToolTip(event.title);
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		browserViewer.setFocus();
	}

	private IProgressMonitor getStatusBarProgressMonitor() {
		IStatusLineManager statusLineManager = getViewSite().getActionBars().getStatusLineManager();
		return statusLineManager.getProgressMonitor();
	}
	
	public boolean close() {
		try {
			getSite().getPage().hideView(this);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void setURL(String url) {
		browserViewer.setURL(url);
	}

}
