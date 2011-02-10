/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * Optimized rule to match a single character. Faster than a RegexpRule for one char.
 * 
 * @author Chris Williams
 */
public class SingleCharacterRule implements IPredicateRule
{

	private IToken successToken;
	private char c;

	public SingleCharacterRule(char c, IToken successToken)
	{
		this.c = c;
		this.successToken = successToken;
	}

	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		if (c == (char) scanner.read())
		{
			return getSuccessToken();
		}
		scanner.unread();
		return Token.UNDEFINED;
	}

	public IToken getSuccessToken()
	{
		return successToken;
	}

	public IToken evaluate(ICharacterScanner scanner)
	{
		return evaluate(scanner, false);
	}
}
