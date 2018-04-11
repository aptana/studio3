/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.preview.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Max Stepanov
 *
 */
public final class EditorUtils {

	/**
	 * 
	 */
	private EditorUtils() {
	}
	
	public static IEditorPart[] findEditors(IEditorInput editorInput, String editorId) {
		int flags = IWorkbenchPage.MATCH_NONE;
		if (editorInput != null) {
			flags |= IWorkbenchPage.MATCH_INPUT;
		}
		if (editorId != null) {
			flags |= IWorkbenchPage.MATCH_ID;
		}
		List<IEditorPart> list = new ArrayList<IEditorPart>();
		for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				for (IEditorReference editorRef : page.findEditors(editorInput, editorId, flags)) {
					IEditorPart editorPart = editorRef.getEditor(false);
					if (editorPart != null) {
						list.add(editorPart);
					}
				}
			}
		}
		return list.toArray(new IEditorPart[list.size()]);
	}

}
