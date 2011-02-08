/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

/**
 * JSONPropertyRule
 */
public class JSONPropertyRule implements IPredicateRule
{
	private IRule _singleQuotedRule;
	private IRule _doubleQuotedRule;
	private IToken _token;
	private IToken _successToken;

	/**
	 * JSONPropertyRule
	 * 
	 * @param singleQuotedToken
	 * @param doubleQuotedToken
	 * @param token
	 */
	public JSONPropertyRule(IToken singleQuotedToken, IToken doubleQuotedToken, IToken token)
	{
		this._singleQuotedRule = new SingleLineRule("'", "'", singleQuotedToken, '\\'); //$NON-NLS-1$ //$NON-NLS-2$
		this._doubleQuotedRule = new SingleLineRule("\"", "\"", doubleQuotedToken, '\\'); //$NON-NLS-1$ //$NON-NLS-2$
		this._token = token;
		this._successToken = Token.UNDEFINED;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner)
	{
		// try double-quoted string
		IToken token = this._doubleQuotedRule.evaluate(scanner);

		// try single-quoted string
		if (token == Token.UNDEFINED)
		{
			token = this._singleQuotedRule.evaluate(scanner);
		}

		// now perform positive lookahead for colon
		if (token != Token.UNDEFINED)
		{
			char c = (char) scanner.read();
			int count = 1;

			// skip any whitespace
			while (Character.isWhitespace(c))
			{
				c = (char) scanner.read();
				count++;
			}

			if (c == ':')
			{
				token = this._token;
			}

			// rewind from lookahead
			for (int i = 0; i < count; i++)
			{
				scanner.unread();
			}
		}

		return this._successToken = token;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IPredicateRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner,
	 * boolean)
	 */
	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		return evaluate(scanner);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IPredicateRule#getSuccessToken()
	 */
	public IToken getSuccessToken()
	{
		return this._successToken;
	}
}
