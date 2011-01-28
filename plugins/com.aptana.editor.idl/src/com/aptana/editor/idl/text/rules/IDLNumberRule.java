/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.idl.text.rules;

import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.common.text.rules.ExtendedWordRule;

public class IDLNumberRule extends ExtendedWordRule
{
	// NOTE: The following regular expression is a combined form of the integer
	// and float definitions from the WebIDL specification (http://dev.w3.org/2006/webapi/WebIDL/#idl-grammar).
	// This corrects those patterns to allow negation in a few cases where they
	// fail using the spec's patterns
	private static final String REGEXP = "-?( 0 ([0-7]* | [Xx][0-9A-Fa-f]+) | [1-9][0-9]* | ([0-9]+\\.[0-9]* | [0-9]*\\.[0-9]+)([Ee][+-]?[0-9]+)? | [0-9]+[Ee][+-]?[0-9]+ )"; //$NON-NLS-1$
	private static Pattern pattern;

	public IDLNumberRule(IToken token)
	{
		super(new IDLNumberDetector(), token, false);
	}

	@Override
	protected boolean wordOK(String word, ICharacterScanner scanner)
	{
		return getPattern().matcher(word).matches();
	}

	private synchronized static Pattern getPattern()
	{
		if (pattern == null)
		{
			pattern = Pattern.compile(REGEXP, Pattern.COMMENTS);
		}
		return pattern;
	}
}