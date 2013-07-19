package com.aptana.preview;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

/**
 * @author Max Stepanov
 * 
 */
public class PreviewEditorMatchingStrategy implements IEditorMatchingStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IEditorMatchingStrategy#matches(org.eclipse.ui.
	 * IEditorReference, org.eclipse.ui.IEditorInput)
	 */
	public boolean matches(IEditorReference editorRef, IEditorInput input) {
		if (input instanceof PreviewEditorInput) {
			PreviewEditorInput pei = (PreviewEditorInput) input;
			try {
				PreviewEditorInput editorInput = (PreviewEditorInput) editorRef.getEditorInput();
				if (editorInput.isFixed() || pei.isFixed()) {
					return editorInput.equals(pei);
				}
			} catch (PartInitException e) {
			}
			return true;
		}
		return false;
	}

}
