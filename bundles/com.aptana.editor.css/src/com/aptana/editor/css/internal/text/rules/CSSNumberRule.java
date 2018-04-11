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
import org.eclipse.jface.text.rules.IWordDetector;

import com.aptana.editor.common.text.rules.ExtendedWordRule;

/**
 * CSSNumberRule
 */
public class CSSNumberRule extends ExtendedWordRule
{
	private static class CSSNumberWordDetector implements IWordDetector
	{
		public boolean isWordStart(char c)
		{
			return c == '-' || c == '+' || c == '.' || Character.isDigit(c);
		}

		public boolean isWordPart(char c)
		{
			return c == '.' || Character.isDigit(c);
		}
	}

	private Pattern pattern;

	/**
	 * CSSNumberRule
	 * 
	 * @param token
	 */
	public CSSNumberRule(IToken token)
	{
		super(new CSSNumberWordDetector(), token, false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.ExtendedWordRule#wordOK(java.lang.String,
	 * org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	protected boolean wordOK(String word, ICharacterScanner scanner)
	{
		if (pattern == null)
		{
			pattern = Pattern.compile("[-+]?\\s*[0-9]+(\\.[0-9]+)?"); //$NON-NLS-1$
		}

		return pattern.matcher(word).matches();
	}
}
