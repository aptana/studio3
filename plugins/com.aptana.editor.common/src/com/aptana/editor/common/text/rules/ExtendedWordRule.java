/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.rules;

import java.util.Iterator;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

public abstract class ExtendedWordRule extends WordRule
{

	/** Buffer used for pattern detection. */
	private StringBuffer fBuffer = new StringBuffer();
	/**
	 * Tells whether this rule is case sensitive.
	 */
	private boolean fIgnoreCase = false;

	/**
	 * Creates a rule which, with the help of a word detector, will return the token associated with the detected word.
	 * If no token has been associated, the specified default token will be returned.
	 * 
	 * @param detector
	 *            the word detector to be used by this rule, may not be <code>null</code>
	 * @param defaultToken
	 *            the default token to be returned on success if nothing else is specified, may not be <code>null</code>
	 * @param ignoreCase
	 *            the case sensitivity associated with this rule
	 * @see #addWord(String, IToken)
	 */
	public ExtendedWordRule(IWordDetector detector, IToken defaultToken, boolean ignoreCase)
	{
		super(detector, defaultToken, ignoreCase);
	}

	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	@SuppressWarnings("unchecked")
	public IToken evaluate(ICharacterScanner scanner)
	{
		int c = scanner.read();
		if (c != ICharacterScanner.EOF && fDetector.isWordStart((char) c))
		{
			if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1))
			{

				fBuffer.setLength(0);
				do
				{
					fBuffer.append((char) c);
					c = scanner.read();
				}
				while (c != ICharacterScanner.EOF && fDetector.isWordPart((char) c));
				scanner.unread();

				String buffer = fBuffer.toString();
				if (!wordOK(buffer, scanner))
				{
					unreadBuffer(scanner);
					return Token.UNDEFINED;
				}
				IToken token = (IToken) fWords.get(buffer);

				if (token == null && fIgnoreCase)
				{
					Iterator<String> iter = fWords.keySet().iterator();
					while (iter.hasNext())
					{
						String key = iter.next();
						if (buffer.equalsIgnoreCase(key))
						{
							token = (IToken) fWords.get(key);
							break;
						}
					}
				}

				if (token != null)
					return token;

				if (fDefaultToken.isUndefined())
					unreadBuffer(scanner);

				return fDefaultToken;
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
	}

	/**
	 * This method is called when we think we've detected a full word. This allows us to get the word in whole plus the
	 * context of the scanner to determine if the word should be accepted.
	 * 
	 * @param word
	 * @param scanner
	 * @return
	 */
	protected abstract boolean wordOK(String word, ICharacterScanner scanner);

	/**
	 * Returns the characters in the buffer to the scanner.
	 * 
	 * @param scanner
	 *            the scanner to be used
	 */
	protected void unreadBuffer(ICharacterScanner scanner)
	{
		for (int i = fBuffer.length() - 1; i >= 0; i--)
			scanner.unread();
	}

}
