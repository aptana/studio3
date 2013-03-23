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

import com.aptana.core.util.StringUtil;
import com.aptana.js.core.JSLanguageConstants;

/**
 * Special "word" detector for finding JS operators.
 * 
 * @author cwilliams
 */
public class JSOperatorDetector implements IWordDetector
{
	StringBuilder buffer = new StringBuilder();
	Set<String> prefixes = new HashSet<String>();

	/**
	 * JSOperatorDetector
	 */
	public JSOperatorDetector()
	{
		for (String operator : JSLanguageConstants.OPERATORS)
		{
			addWord(operator);
		}
	}

	/**
	 * addWord
	 * 
	 * @param word
	 */
	public void addWord(String word)
	{
		if (!StringUtil.isEmpty(word))
		{
			for (int i = 1; i < word.length(); i++)
			{
				prefixes.add(word.substring(0, i));
			}

			prefixes.add(word);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	public boolean isWordPart(char c)
	{
		buffer.append(c);

		return prefixes.contains(buffer.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	public boolean isWordStart(char c)
	{
		buffer.setLength(0);

		return isWordPart(c);
	}
}