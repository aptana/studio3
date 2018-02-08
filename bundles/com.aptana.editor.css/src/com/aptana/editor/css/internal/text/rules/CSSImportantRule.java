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
 * CSSImportantRule
 */
public class CSSImportantRule extends ExtendedWordRule
{
	private static class ImportantWordDetector implements IWordDetector
	{
		public boolean isWordStart(char c)
		{
			return c == '!';
		}

		public boolean isWordPart(char c)
		{
			return isWordStart(c) || Character.isLetterOrDigit(c) || Character.isWhitespace(c);
		}
	}

	private Pattern pattern;

	/**
	 * CSSImportantRule
	 * 
	 * @param token
	 */
	public CSSImportantRule(IToken token)
	{
		super(new ImportantWordDetector(), token, true);
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
			pattern = Pattern.compile("!\\s*important", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
		}

		return pattern.matcher(word).matches();
	}
}
