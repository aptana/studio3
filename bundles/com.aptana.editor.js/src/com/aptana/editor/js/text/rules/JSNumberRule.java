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

public class JSNumberRule extends ExtendedWordRule
{
	// @formatter:off
	private static Pattern NUMBER_PATTERN = Pattern.compile("(?:(?:0[xX][0-9a-fA-F]+)|(?:(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)(?:[eE][-+]?\\d+)?))"); //$NON-NLS-1$;
	// @formatter:on

	/**
	 * JSNumberRule
	 * 
	 * @param token
	 */
	public JSNumberRule(IToken token)
	{
		super(new JSNumberDetector(), token, false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.ExtendedWordRule#wordOK(java.lang.String,
	 * org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	protected boolean wordOK(String word, ICharacterScanner scanner)
	{
		return NUMBER_PATTERN.matcher(word).matches();
	}
}