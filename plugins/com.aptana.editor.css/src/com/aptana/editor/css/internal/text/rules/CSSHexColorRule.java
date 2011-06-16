/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.internal.text.rules;

import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.common.text.rules.ExtendedWordRule;

/**
 * CSSHexColorRule
 */
public class CSSHexColorRule extends ExtendedWordRule
{
	private final Pattern HEX_COLOR = Pattern.compile("#[0-9a-fA-F]+"); //$NON-NLS-1$

	/**
	 * CSSHexColorRule
	 * 
	 * @param token
	 */
	public CSSHexColorRule(IToken token)
	{
		super(new IdentifierWithPrefixDetector('#'), token, false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.ExtendedWordRule#wordOK(java.lang.String,
	 * org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	protected boolean wordOK(String word, ICharacterScanner scanner)
	{
		boolean result = false;

		if (word.length() == 4 || word.length() == 7)
		{
			result = HEX_COLOR.matcher(word).matches();
		}

		return result;
	}
}
