/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.markdown.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.common.text.rules.ExtendedWordRule;

public class EscapeCharacterRule extends ExtendedWordRule
{
	public EscapeCharacterRule(IToken defaultToken)
	{
		super(new EscapeCharacterDetector(), defaultToken, false);
	}

	@Override
	protected boolean wordOK(String word, ICharacterScanner scanner)
	{
		if (word.length() != 2)
		{
			return false;
		}
		char c = word.charAt(1);
		switch (c)
		{
			case '\\':
			case '`':
			case '*':
			case '_':
			case '{':
			case '}':
			case '[':
			case ']':
			case '(':
			case ')':
			case '#':
			case '+':
			case '-':
			case '.':
			case '!':
				return true;
			default:
				return false;
		}
	}
}