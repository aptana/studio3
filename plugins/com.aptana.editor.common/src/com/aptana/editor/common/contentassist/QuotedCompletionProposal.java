/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.graphics.Image;

import com.aptana.parsing.lexer.IRange;

public class QuotedCompletionProposal extends CommonCompletionProposal
{

	private boolean addLeading;
	private boolean addTrailing;

	public QuotedCompletionProposal(String name, String description, String location, IRange range, Image image,
			Image[] userAgents, boolean addleading, boolean addTrailing)
	{
		super(name, range.getStartingOffset(), range.getLength(), name.length(), image, name, null, description);
		setFileLocation(location);
		setUserAgentImages(userAgents);
		this.addLeading = addleading;
		this.addTrailing = addTrailing;
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
					if (addLeading)
					{
						// Add wrapping quotes
						_replacementString = "\"" + _replacementString; //$NON-NLS-1$
						_cursorPosition++;
					}
					break;
			}
			if (addTrailing)
			{
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
						case ' ':
						case '\t':
						case '\r':
						case '\n':
						case '\f':
							// add close quote
							_replacementString += quote;
							_cursorPosition++;
							break;

						default:
							if (addEndQuote(nextChar))
							{
								// Add a close quote and then a space
								_replacementString += quote + " "; //$NON-NLS-1$
								_cursorPosition += 2;
							}
							break;
					}
				}
			}

		}
		catch (BadLocationException e)
		{
			// ignore
		}
		super.apply(viewer, trigger, stateMask, offset);
	}

	protected boolean addEndQuote(char nextChar)
	{
		switch (nextChar)
		{
			case '\'':
			case '"':
				return false;

			default:
				return true;
		}
	}
}
