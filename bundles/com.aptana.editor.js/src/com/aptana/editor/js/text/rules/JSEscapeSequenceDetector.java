// $codepro.audit.disable characterComparison
/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text.rules;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.rules.IWordDetector;

class JSEscapeSequenceDetector implements IWordDetector
{
	private static final Set<Character> CHARS;

	/**
	 * static initializer
	 */
	static
	{
		CHARS = new HashSet<Character>();

		// type specifier
		CHARS.add('x');
		CHARS.add('u');

		// digits
		for (char c = '0'; c <= '9'; c++)
		{
			CHARS.add(c);
		}

		// uppercase hex
		for (char c = 'A'; c <= 'F'; c++)
		{
			CHARS.add(c);
		}

		// lowercase hex
		for (char c = 'a'; c <= 'f'; c++)
		{
			CHARS.add(c);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	public boolean isWordStart(char c)
	{
		return c == '\\';
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	public boolean isWordPart(char c)
	{
		return CHARS.contains(c);
	}
}