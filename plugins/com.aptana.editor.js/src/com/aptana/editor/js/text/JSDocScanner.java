/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

/**
 * A rule based JavaDoc scanner.
 */
public class JSDocScanner extends RuleBasedScanner
{
	/**
	 * A key word detector.
	 */
	static class JSDocWordDetector implements IWordDetector
	{
		/*
		 * (non-Javadoc) Method declared on IWordDetector
		 */
		public boolean isWordStart(char c)
		{
			return c == '@';
		}

		/*
		 * (non-Javadoc) Method declared on IWordDetector
		 */
		public boolean isWordPart(char c)
		{
			return Character.isLetter(c);
		}
	}

	@SuppressWarnings("nls")
	private static String[] KEYWORDS = { "@author", "@class", "@constructor", "@deprecated", "@exception", "@extends",
			"@final", "@member", "@param", "@private", "@requires", "@return", "@returns", "@see", "@serial",
			"@serialData", "@serialField", "@since", "@throws", "@type", "@version" };

	/**
	 * Create a new javadoc scanner for the given color provider.
	 */
	public JSDocScanner()
	{
		super();

		List<IRule> list = new ArrayList<IRule>();

		// Add rule for tags.
		list.add(new SingleLineRule("<", ">", getToken("text.html.basic"))); //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-1$

		// Add rule for links.
		list.add(new SingleLineRule("{", "}", getToken("markup.underline.link"))); //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-1$

		// Add word rule for keywords.
		IToken keyword = getToken("meta.tag.documentation.js"); //$NON-NLS-1$
		WordRule wordRule = new WordRule(new JSDocWordDetector());
		for (String word : KEYWORDS)
		{
			wordRule.addWord(word, keyword);
		}
		list.add(wordRule);

		setDefaultReturnToken(getToken("comment.block.documentation.js")); //$NON-NLS-1$
		setRules(list.toArray(new IRule[list.size()]));
	}

	protected IToken getToken(String tokenName)
	{
		return new Token(tokenName);
	}

}
