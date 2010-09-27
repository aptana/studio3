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
package com.aptana.editor.erb.html.parsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.erb.parsing.lexer.ERBTokens;
import com.aptana.editor.html.parsing.HTMLTokenScanner;

public class RHTMLTokenScanner extends HTMLTokenScanner
{

	@SuppressWarnings("nls")
	private static final String[] RUBY_START = { "<%", "<%=" };
	@SuppressWarnings("nls")
	private static final String[] RUBY_END = new String[] { "-%>", "%>" };

	public RHTMLTokenScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();
		// adds rules for finding the ruby start and end sequences
		WordRule wordRule = new WordRule(new RubyStartDetector(), Token.UNDEFINED);
		IToken token = createToken(getTokenName(ERBTokens.RUBY));
		for (String word : RUBY_START)
		{
			wordRule.addWord(word, token);
		}
		rules.add(wordRule);
		wordRule = new WordRule(new RubyEndDetector(), Token.UNDEFINED);
		token = createToken(getTokenName(ERBTokens.RUBY_END));
		for (String word : RUBY_END)
		{
			wordRule.addWord(word, token);
		}
		rules.add(wordRule);

		for (IRule rule : fRules)
		{
			rules.add(rule);
		}

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	private static String getTokenName(short token)
	{
		return ERBTokens.getTokenName(token);
	}

	private static final class RubyStartDetector implements IWordDetector
	{

		public boolean isWordPart(char c)
		{
			switch (c)
			{
				case '<':
				case '%':
				case '=':
					return true;
			}
			return false;
		}

		public boolean isWordStart(char c)
		{
			return c == '<';
		}
	}

	private static final class RubyEndDetector implements IWordDetector
	{

		public boolean isWordPart(char c)
		{
			switch (c)
			{
				case '-':
				case '%':
				case '>':
					return true;
			}
			return false;
		}

		public boolean isWordStart(char c)
		{
			return c == '-' || c == '%';
		}
	}
}
