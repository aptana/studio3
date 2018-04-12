/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable closeInFinally

package com.aptana.browser.support;

import java.net.URL;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.browser.InternalBrowserInstance;
import org.eclipse.ui.internal.browser.WebBrowserUIPlugin;

import com.aptana.browser.parts.WebBrowserEditor;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class BrowserEditorInstance extends InternalBrowserInstance {

	/**
	 * @param id
	 * @param style
	 * @param name
	 * @param tooltip
	 */
	public BrowserEditorInstance(String id, int style, String name, String tooltip) {
		super(id, style, name, tooltip);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.browser.IWebBrowser#openURL(java.net.URL)
	 */
	public void openURL(URL url) throws PartInitException {
		WebBrowserEditorInput editorInput = new WebBrowserEditorInput(url, style);
		editorInput.setName(name);
		editorInput.setToolTipText(tooltip);
		WebBrowserEditor editor = (WebBrowserEditor) part;
		IWorkbenchWindow workbenchWindow = WebBrowserUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = null;
		if (workbenchWindow != null) {
			workbenchPage = workbenchWindow.getActivePage();
		}
		if (workbenchPage == null) {
			throw new PartInitException("Cannot get Workbench page"); //$NON-NLS-1$
		}
		if (editor != null) {
			editor.init(editor.getEditorSite(), editorInput);
			workbenchPage.activate(editor);
		} else {
			editor = (WebBrowserEditor) workbenchPage.openEditor(editorInput, WebBrowserEditor.EDITOR_ID);
			hookPart(workbenchPage, editor);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.browser.AbstractWebBrowser#close()
	 */
	@Override
	public boolean close() {
		try {
			return ((WebBrowserEditor) part).close();
		} catch (Exception e) {
			return false;
		}
	}

}
