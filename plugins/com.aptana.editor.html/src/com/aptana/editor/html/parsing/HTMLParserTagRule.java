/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.html.parsing;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.text.rules.CollectingCharacterScanner;
import com.aptana.editor.common.text.rules.ExtendedToken;
import com.aptana.editor.common.text.rules.SequenceCharacterScanner;

class HTMLParserTagRule extends MultiLineRule
{

	private static final IToken singleQuoteStringTOKEN = new Token("SQS"); //$NON-NLS-1$
	private static final IPredicateRule singleQuoteStringRule = new MultiLineRule(
			"'", "'", singleQuoteStringTOKEN, '\\'); //$NON-NLS-1$ //$NON-NLS-2$
	private static final IPredicateRule singleQuoteStringEOLRule = new EndOfLineRule("'", singleQuoteStringTOKEN, '\\'); //$NON-NLS-1$

	private static final IToken doubleQuoteStringTOKEN = new Token("DQS"); //$NON-NLS-1$
	private static final IPredicateRule doubleQuoteStringRule = new MultiLineRule(
			"\"", "\"", doubleQuoteStringTOKEN, '\\'); //$NON-NLS-1$ //$NON-NLS-2$
	private static final IPredicateRule doubleQuoteStringEOLRule = new EndOfLineRule("\"", doubleQuoteStringTOKEN, '\\'); //$NON-NLS-1$

	private boolean fIgnoreCase;
	private int fEmbeddedStart;

	HTMLParserTagRule(IToken token)
	{
		this("", token); //$NON-NLS-1$
	}

	HTMLParserTagRule(String tag, IToken token)
	{
		this(tag, token, false);
	}

	HTMLParserTagRule(String tag, IToken token, boolean ignoreCase)
	{
		this("<" + tag, ">", token, ignoreCase); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected HTMLParserTagRule(String startSequence, String endSequence, IToken token, boolean ignoreCase)
	{
		super(startSequence, endSequence, token);
		fIgnoreCase = ignoreCase;
	}

	@Override
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed)
	{
		boolean detected = true;
		for (int i = 1; i < sequence.length; ++i)
		{
			int c = scanner.read();
			if (c == ICharacterScanner.EOF && eofAllowed)
			{
				break;
			}
			if ((fIgnoreCase && Character.toLowerCase(c) != Character.toLowerCase(sequence[i]))
					|| (!fIgnoreCase && c != sequence[i]))
			{
				// Non-matching character detected, rewind the scanner back to the start.
				// Do not unread the first character.
				scanner.unread();
				for (int j = i - 1; j > 0; --j)
				{
					scanner.unread();
				}
				detected = false;
				break;
			}
		}

		if (!detected)
		{
			return detected;
		}
		if ((sequence.length == 1 && sequence[0] == '<')
				|| (sequence.length == 2 && sequence[0] == '<' && sequence[1] == '/'))
		{
			int nextChar = scanner.read();
			if (nextChar == ICharacterScanner.EOF)
			{
				return false;
			}
			scanner.unread();
			return Character.isJavaIdentifierStart(nextChar);
		}
		return detected;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.PatternRule#endSequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	protected boolean endSequenceDetected(ICharacterScanner scanner)
	{
		CollectingCharacterScanner collectingCharacterScanner = new CollectingCharacterScanner(scanner,
				String.valueOf(fStartSequence));
		int c, length = 0;
		while ((c = collectingCharacterScanner.read()) != ICharacterScanner.EOF)
		{
			length++;
			if (c == '\'')
			{
				collectingCharacterScanner.unread();
				IToken token = singleQuoteStringRule.evaluate(collectingCharacterScanner);
				if (token.isUndefined())
				{
					token = singleQuoteStringEOLRule.evaluate(collectingCharacterScanner);
				}
			}
			else if (c == '"')
			{
				collectingCharacterScanner.unread();
				IToken token = doubleQuoteStringRule.evaluate(collectingCharacterScanner);
				if (token.isUndefined())
				{
					token = doubleQuoteStringEOLRule.evaluate(collectingCharacterScanner);
				}
			}
			else if (c == fStartSequence[0])
			{
				fEmbeddedStart++;
			}
			else if (c == fEndSequence[0])
			{
				if (fEmbeddedStart == 0)
				{
					if (fToken instanceof ExtendedToken)
					{
						((ExtendedToken) fToken).setContents(collectingCharacterScanner.getContents());
					}
					return true;
				}
				fEmbeddedStart--;
			}
		}
		if (scanner instanceof SequenceCharacterScanner && ((SequenceCharacterScanner) scanner).foundSequence())
		{
			// this means the EOF came from seeing a switching sequence, so assumes the end is detected and no need to
			// rewind one character
			if (fToken instanceof ExtendedToken)
			{
				((ExtendedToken) fToken).setContents(collectingCharacterScanner.getContents());
			}
			return true;
		}
		for (int i = 0; i < length; ++i)
		{
			collectingCharacterScanner.unread();
		}
		// unread the original character
		collectingCharacterScanner.unread();
		return false;
	}
}
