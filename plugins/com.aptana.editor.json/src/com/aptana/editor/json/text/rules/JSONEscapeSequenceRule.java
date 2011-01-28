/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.text.rules;

import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.common.text.rules.ExtendedWordRule;

public class JSONEscapeSequenceRule extends ExtendedWordRule
{
	private static final String REGEXP = "\\\\([\"\\/bfnrt]|u[0-9a-fA-F]{4}|.)"; //$NON-NLS-1$
	private static Pattern pattern;

	/**
	 * getPattern
	 * 
	 * @return
	 */
	private synchronized static Pattern getPattern()
	{
		if (pattern == null)
		{
			pattern = Pattern.compile(REGEXP);
		}
		
		return pattern;
	}

	/**
	 * JSEscapeSequenceRule
	 * 
	 * @param token
	 */
	public JSONEscapeSequenceRule(IToken token)
	{
		super(new JSONEscapeSequenceDetector(), token, false);
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