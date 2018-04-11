/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.dtd.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.common.text.rules.ExtendedWordRule;

public class DTDEntityRule extends ExtendedWordRule
{
	private char _startingChararacter;

	/**
	 * DTDEntityRule
	 * 
	 * @param firstChar
	 * @param defaultToken
	 */
	public DTDEntityRule(char firstChar, IToken defaultToken)
	{
		super(new DTDEntityDetector(firstChar), defaultToken, true);

		this._startingChararacter = firstChar;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.ExtendedWordRule#wordOK(java.lang.String,
	 * org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	protected boolean wordOK(String word, ICharacterScanner scanner)
	{
		boolean result = false;

		if (word != null && word.length() >= 2)
		{
			result = (word.charAt(0) == this._startingChararacter) && word.charAt(word.length() - 1) == ';';
		}

		return result;
	}
}
