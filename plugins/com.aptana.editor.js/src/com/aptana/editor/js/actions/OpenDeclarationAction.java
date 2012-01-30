/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.aptana.core.util.ArrayUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.js.JSSourceEditor;
import com.aptana.editor.js.hyperlink.JSHyperlinkDetector;

/**
 * OpenDeclarationAction
 */
public class OpenDeclarationAction extends TextEditorAction
{
	/**
	 * OpenDeclarationAction
	 * 
	 * @param bundle
	 * @param editor
	 */
	public OpenDeclarationAction(ResourceBundle bundle, ITextEditor editor)
	{
		super(bundle, "openDeclaration_", editor); //$NON-NLS-1$
	}

	/**
	 * Open the declaration if possible.
	 */
	@Override
	public void run()
	{
		ITextEditor textEditor = getTextEditor();

		if (textEditor instanceof JSSourceEditor)
		{
			ITextSelection selection = (ITextSelection) textEditor.getSelectionProvider().getSelection();
			IRegion region = new Region(selection.getOffset(), 1);
			JSHyperlinkDetector detector = new JSHyperlinkDetector();
			IHyperlink[] hyperlinks = detector.detectHyperlinks((AbstractThemeableEditor) textEditor, region, true);

			if (!ArrayUtil.isEmpty(hyperlinks))
			{
				// give first link highest precedence
				hyperlinks[0].open();
			}
		}
	}
}
