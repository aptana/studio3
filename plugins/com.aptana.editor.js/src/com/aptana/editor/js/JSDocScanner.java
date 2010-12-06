/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

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
			return (c == '@');
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
