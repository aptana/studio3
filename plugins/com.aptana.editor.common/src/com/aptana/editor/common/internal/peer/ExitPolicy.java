/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.peer;

import java.util.Stack;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.swt.events.VerifyEvent;

import com.aptana.editor.common.internal.peer.PeerCharacterCloser.BracketLevel;

/**
 * Determines when to exit linked mode in the editor.
 * 
 * @author cwilliams
 */
class ExitPolicy implements IExitPolicy
{

	/**
	 * Integer constants for the special "curly" quotes. See http://www.dwheeler.com/essays/quotes-in-html.html
	 */
	private static final char CURLY_RIGHT_SINGLE_QUOTE = '\u2019';
	private static final char CURLY_RIGHT_DOUBLE_QUOTE = '\u201D';
	
	private ITextViewer fTextViewer;
	private final char fExitCharacter;
	private final char fEscapeCharacter;
	private final Stack<BracketLevel> fStack;
	private final int fSize;

	public ExitPolicy(ITextViewer textViewer, char exitCharacter, char escapeCharacter, Stack<BracketLevel> stack)
	{
		fTextViewer = textViewer;
		fExitCharacter = exitCharacter;
		fEscapeCharacter = escapeCharacter;
		fStack = stack;
		if (fStack != null)
		{
			fSize = fStack.size();
		}
		else
		{
			fSize = 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy#doExit(org.eclipse.jface.text.link.LinkedModeModel,
	 * org.eclipse.swt.events.VerifyEvent, int, int)
	 */
	public ExitFlags doExit(LinkedModeModel model, VerifyEvent event, int offset, int length)
	{
		if (shouldInsertNewline())
		{
			if (event.character == '\n' || event.character == '\r')
			{
				return new ExitFlags(ILinkedModeListener.EXIT_ALL, true);
			}
		}

		if (event.character != fExitCharacter)
			return null;

		if (fSize == fStack.size() && !isEscaped(offset))
		{
			BracketLevel level = fStack.peek();
			if (offset < level.fFirstPosition.offset || level.fSecondPosition.offset < offset)
				return null;
			if (level.fSecondPosition.offset == offset && length == 0)
				// don't enter the character if it is the closing peer
				return new ExitFlags(ILinkedModeListener.UPDATE_CARET, false);
		}

		return null;
	}

	/**
	 * Don't insert newline when we're in auto-paired string chars, pipes ||, or less-than/greater-than <> pair.
	 * 
	 * @return
	 */
	private boolean shouldInsertNewline()
	{
		return !isStringPair() && fExitCharacter != '|' && fExitCharacter != '>';
	}

	private boolean isStringPair()
	{
		return fExitCharacter == '"' || fExitCharacter == '\'' || fExitCharacter == '`'
				|| fExitCharacter == CURLY_RIGHT_DOUBLE_QUOTE || fExitCharacter == CURLY_RIGHT_SINGLE_QUOTE;
	}

	/**
	 * Is the character escaped by a preceding escape char?
	 * 
	 * @param offset
	 * @return
	 */
	private boolean isEscaped(int offset)
	{
		IDocument document = fTextViewer.getDocument();
		try
		{
			return fEscapeCharacter == document.getChar(offset - 1);
		}
		catch (BadLocationException e)
		{
		}
		return false;
	}
}