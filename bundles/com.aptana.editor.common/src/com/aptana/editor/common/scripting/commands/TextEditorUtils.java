/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.commands;

import java.lang.reflect.Method;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.extensions.IThemeableEditor;

/**
 * This holds the TextEditor related utilities.
 * 
 * @author schitale
 */
public final class TextEditorUtils
{

	private TextEditorUtils()
	{
	}

	/**
	 * Tries to get the accurate location of the caret relative to the document.
	 * <p>
	 * It tries to use the caret position in the StyledText and the selection to determine if the non-zero length
	 * selection is forward (LtoR) or backward (RtoL) and uses that info to compute the location.
	 * 
	 * @param textEditor
	 * @return -1 if editor is null or there is no selection. Otherwise uses caret offset from selection.
	 */
	public static int getCaretOffset(ITextEditor textEditor)
	{
		if (textEditor == null)
			return -1;
		// Assume forward (LtoR) selection
		boolean forwardSelection = true;
		Object adapter = textEditor.getAdapter(Control.class);
		if (adapter instanceof StyledText)
		{
			// Accurate
			StyledText styledText = (StyledText) adapter;
			int caretOffset = styledText.getCaretOffset();
			Point selection = styledText.getSelection();
			forwardSelection = (caretOffset == selection.y);
		}
		ISelection selection = textEditor.getSelectionProvider().getSelection();
		if (selection instanceof ITextSelection)
		{
			ITextSelection textSelection = (ITextSelection) selection;
			// Inaccurate. This can happen when the selection is
			// reverse i.e. from higher offset to lower offset
			if (forwardSelection)
			{
				return textSelection.getOffset() + textSelection.getLength();
			}
			return textSelection.getOffset();
		}
		return -1;
	}

	public static ISourceViewer getSourceViewer(ITextEditor textEditor)
	{
		if (textEditor instanceof IThemeableEditor)
		{
			IThemeableEditor editor = (IThemeableEditor) textEditor;
			return editor.getISourceViewer();
		}
		if (textEditor instanceof AbstractTextEditor)
		{
			try
			{
				Method m = AbstractTextEditor.class.getDeclaredMethod("getSourceViewer"); //$NON-NLS-1$
				m.setAccessible(true);
				return (ISourceViewer) m.invoke(textEditor);
			}
			catch (Exception e)
			{
				// ignore
			}
		}
		return null;
	}
}
