/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.internal.peer.CharacterPairMatcher;
import com.aptana.ui.util.UIUtils;

/**
 * A handler for the Goto matching bracket.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class GotoMatchingBracketHandler extends AbstractHandler
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IEditorPart activeEditor = UIUtils.getActiveEditor();
		if (activeEditor instanceof AbstractThemeableEditor)
		{
			AbstractThemeableEditor editor = (AbstractThemeableEditor) activeEditor;
			CharacterPairMatcher characterPairMatcher = new CharacterPairMatcher(editor.getPairMatchingCharacters());
			IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
			IRegion match = characterPairMatcher.match(document, editor.getCaretOffset());
			if (match != null)
			{
				if (characterPairMatcher.getAnchor() == CharacterPairMatcher.RIGHT)
				{
					editor.selectAndReveal(match.getOffset() + 1, 0);
				}
				else
				{
					editor.selectAndReveal(match.getOffset() + match.getLength(), 0);
				}
			}
		}
		return null;
	}
}
