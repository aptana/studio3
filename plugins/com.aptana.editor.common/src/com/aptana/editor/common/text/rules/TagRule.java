/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;

public class TagRule extends MultiLineRule
{

	private static final IToken singleQuoteStringTOKEN = new Token("SQS"); //$NON-NLS-1$
	private static final IPredicateRule singleQuoteStringRule = new MultiLineRule("'", "'", singleQuoteStringTOKEN, '\\'); //$NON-NLS-1$ //$NON-NLS-2$
	private static final IPredicateRule singleQuoteStringEOLRule = new EndOfLineRule("'", singleQuoteStringTOKEN, '\\'); //$NON-NLS-1$

	private static final IToken doubleQuoteStringTOKEN = new Token("DQS"); //$NON-NLS-1$
	private static final IPredicateRule doubleQuoteStringRule = new MultiLineRule("\"", "\"", doubleQuoteStringTOKEN, '\\'); //$NON-NLS-1$ //$NON-NLS-2$
	private static final IPredicateRule doubleQuoteStringEOLRule = new EndOfLineRule("\"", doubleQuoteStringTOKEN, '\\'); //$NON-NLS-1$

	private boolean fIgnoreCase;

	public TagRule(IToken token)
	{
		this("", token); //$NON-NLS-1$
	}

	public TagRule(String tag, IToken token)
	{
		this(tag, token, false);
	}

	public TagRule(String tag, IToken token, boolean ignoreCase)
	{
		super("<" + tag, ">", token); //$NON-NLS-1$ //$NON-NLS-2$
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
		int c;
		while ((c = scanner.read()) != ICharacterScanner.EOF)
		{
			if (c == '\'')
			{
				scanner.unread();
				IToken token = singleQuoteStringRule.evaluate(scanner);
				if (token.isUndefined())
				{
					token = singleQuoteStringEOLRule.evaluate(scanner);
				}
			}
			else if (c == '"')
			{
				scanner.unread();
				IToken token = doubleQuoteStringRule.evaluate(scanner);
				if (token.isUndefined())
				{
					token = doubleQuoteStringEOLRule.evaluate(scanner);
				}
			}
			else if (c == '>')
			{
				return true;
			}
		}
		if (scanner instanceof SequenceCharacterScanner && ((SequenceCharacterScanner) scanner).foundSequence())
		{
			// this means the EOF came from seeing a switching sequence, so assumes the end is detected and no need to
			// rewind one character
			return true;
		}
		scanner.unread();
		return false;
	}
}
