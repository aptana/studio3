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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.eclipse.ui.part.EditorPart;

import com.aptana.browser.WebBrowserViewer;
import com.aptana.swt.webkitbrowser.WebKitBrowser;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class WebBrowserEditor extends EditorPart {

	public static final String EDITOR_ID = "com.aptana.browser.editors.webbrowser"; //$NON-NLS-1$
	
	protected WebBrowserViewer webBrowser;
	private int progressWorked;
	private String initialURL;
	private Image image;
	private boolean disposed;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		if (input instanceof WebBrowserEditorInput) {
			WebBrowserEditorInput wbei = (WebBrowserEditorInput) input;
			initialURL = null;
			if (wbei.getURL() != null)
				initialURL = wbei.getURL().toExternalForm();
			if (webBrowser != null) {
				webBrowser.setURL(initialURL);
				site.getWorkbenchWindow().getActivePage().activate(this);
			}
	
			setPartName(wbei.getName());
			setTitleToolTip(wbei.getToolTipText());
			Image oldImage = image;
			ImageDescriptor id = wbei.getImageDescriptor();
			image = id.createImage();

			setTitleImage(image);
			if (oldImage != null && !oldImage.isDisposed())
				oldImage.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (image != null && !image.isDisposed()) {
			image.dispose();
			image = null;
		}
		super.dispose();
		disposed = true;
	}

	public boolean isDisposed() {
		return disposed;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		WebBrowserEditorInput input = getWebBrowserEditorInput();
		int style = 0;
		if (input == null || input.isLocationBarLocal()) {
			style |= WebBrowserViewer.NAVIGATION_BAR;
		}
		if (input == null || input.isToolbarLocal()) {
			style |= WebBrowserViewer.NAVIGATION_BAR;
		}
		webBrowser = new WebBrowserViewer(parent, style);
		WebKitBrowser browser = (WebKitBrowser) webBrowser.getBrowser();
		browser.addProgressListener(new ProgressListener() {
			public void changed(ProgressEvent event) {
				if (event.total == 0) {
					return;
				}
				if (event.current == 0) {
					IProgressMonitor progressMonitor = getStatusBarProgressMonitor();
					progressMonitor.done();
					progressMonitor.beginTask("", event.total); //$NON-NLS-1$
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
		webBrowser.setURL(initialURL);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		if (webBrowser != null) {
			webBrowser.setFocus();
		}
	}

	private IProgressMonitor getStatusBarProgressMonitor() {
		IStatusLineManager statusLineManager = getEditorSite().getActionBars().getStatusLineManager();
		return statusLineManager.getProgressMonitor();
	}

	public boolean close() {
        final boolean [] result = new boolean[1];
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				result[0] = getEditorSite().getPage().closeEditor(WebBrowserEditor.this, false);
			}
		});
        return result[0];
	}

	protected WebBrowserEditorInput getWebBrowserEditorInput() {
		IEditorInput input = getEditorInput();
		if (input instanceof WebBrowserEditorInput)
			return (WebBrowserEditorInput) input;
		return null;
	}

}
