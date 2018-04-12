/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.contentassist.index.IHTMLIndexConstants;
import com.aptana.editor.html.contentassist.model.ValueElement;
import com.aptana.parsing.lexer.IRange;

public class HTMLAttributeValueProposal extends CommonCompletionProposal
{

	private static final Image ATTRIBUTE_ICON = HTMLPlugin.getImage("/icons/attribute.png"); //$NON-NLS-1$

	public HTMLAttributeValueProposal(ValueElement value, IRange range, Image[] userAgents)
	{
		super(value.getName(), range.getStartingOffset(), range.getLength(), value.getName().length(), ATTRIBUTE_ICON,
				value.getName(), null, value.getDescription());
		setFileLocation(IHTMLIndexConstants.CORE);
		setUserAgentImages(userAgents);
	}

	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset)
	{
		try
		{
			IDocument document = viewer.getDocument();
			// Handle wrapping in quotes if necessary
			char prevChar = _replacementOffset > 0 ? document.getChar(_replacementOffset - 1) : ' ';
			char quote = '"';
			switch (prevChar)
			{
				case '\'':
					quote = '\'';
					break;
				case '"':
					// We're fine
					break;

				default:
					// Add wrapping quotes
					_replacementString = "\"" + _replacementString; //$NON-NLS-1$
					_cursorPosition++;
					break;
			}
			// handle adding trailing space if necessary
			int nextCharIndex = _replacementOffset + _replacementLength;
			if (nextCharIndex >= document.getLength())
			{
				// Add a close quote when we're against the EOF
				_replacementString += quote;
				_cursorPosition++;
			}
			else
			{
				char nextChar = document.getChar(nextCharIndex);
				switch (nextChar)
				{
					case '\'':
					case '"':
						// no need to add quote
						break;
					case ' ':
					case '\t':
					case '>':
					case '/':
						// add close quote
						_replacementString += quote;
						_cursorPosition++;
						break;

					default:
						// Add a close quote and then a space
						_replacementString += quote + " "; //$NON-NLS-1$
						_cursorPosition += 2;
						break;
				}
			}

		}
		catch (BadLocationException e)
		{
			// ignore
		}
		super.apply(viewer, trigger, stateMask, offset);
	}
}
