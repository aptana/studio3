/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text.rules;

import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.common.text.rules.ExtendedWordRule;

public class JSEscapeSequenceRule extends ExtendedWordRule
{
	private static final String REGEXP = "\\\\(x[0-9a-fA-F]{2}|u[0-9a-fA-F]{4}|[0-2][0-7]{0,2}|3[0-6][0-7]|37[0-7]?|[4-7][0-7]?|.)"; //$NON-NLS-1$
	private static Pattern PATTERN;

	/**
	 * getPattern
	 * 
	 * @return
	 */
	private synchronized static Pattern getPattern()
	{
		if (PATTERN == null)
		{
			PATTERN = Pattern.compile(REGEXP);
		}
		return PATTERN;
	}

	/**
	 * JSEscapeSequenceRule
	 * 
	 * @param token
	 */
	public JSEscapeSequenceRule(IToken token)
	{
		super(new JSEscapeSequenceDetector(), token, false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.ExtendedWordRule#wordOK(java.lang.String,
	 * org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	protected boolean wordOK(String word, ICharacterScanner scanner)
	{
		return getPattern().matcher(word).matches();
	}
}