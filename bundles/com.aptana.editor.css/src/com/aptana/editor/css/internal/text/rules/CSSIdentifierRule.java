/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.internal.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.common.text.rules.ExtendedWordRule;

/**
 * CSSIdentifierRule
 */
public class CSSIdentifierRule extends ExtendedWordRule
{
	/**
	 * CSSIdentifierRule
	 * 
	 * @param token
	 */
	public CSSIdentifierRule(IToken token)
	{
		super(new KeywordIdentifierDetector(), token, false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.ExtendedWordRule#wordOK(java.lang.String,
	 * org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	protected boolean wordOK(String word, ICharacterScanner scanner)
	{
		if (word == null || word.length() == 0)
		{
			return false;
		}

		if (word.charAt(0) == '-')
		{
			return word.length() > 1;
		}

		return true;
	}
}
