/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.rules;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class CharacterMapRule implements IPredicateRule
{
	private Map<Character, IToken> characterTokenMap;
	private IToken successToken;

	/**
	 * CharacterMapRule
	 */
	public CharacterMapRule()
	{
		characterTokenMap = new HashMap<Character, IToken>();
	}

	/**
	 * add
	 * 
	 * @param c
	 * @param token
	 */
	public CharacterMapRule add(char c, IToken token)
	{
		characterTokenMap.put(c, token);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IPredicateRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner,
	 * boolean)
	 */
	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		successToken = characterTokenMap.get((char) scanner.read());

		if (successToken == null)
		{
			scanner.unread();
			successToken = Token.UNDEFINED;
		}

		return successToken;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IPredicateRule#getSuccessToken()
	 */
	public IToken getSuccessToken()
	{
		return successToken;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner)
	{
		return evaluate(scanner, false);
	}
}
