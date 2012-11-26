/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.xml.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * @author Max Stepanov
 */
public class BrokenStringRule implements IRule
{

	private final IToken singleQuoteToken;
	private final IToken doubleQuoteToken;

	/**
	 * 
	 */
	public BrokenStringRule(IToken singleQuoteToken, IToken doubleQuoteToken)
	{
		this.singleQuoteToken = singleQuoteToken;
		this.doubleQuoteToken = doubleQuoteToken;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner)
	{
		int readCount = 1;
		int c;
		while ((c = scanner.read()) != ICharacterScanner.EOF)
		{
			if (c == '\'')
			{
				return singleQuoteToken;
			}
			else if (c == '"')
			{
				return doubleQuoteToken;
			}
			else if (c == '=')
			{
				break;
			}
			else if (c == '<' && readCount == 1)
			{
				break;
			}
			++readCount;
		}
		while (0 < readCount--)
		{
			scanner.unread();
		}
		return Token.UNDEFINED;
	}

}
