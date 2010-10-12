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
package com.aptana.editor.beaver;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.beaver.parsing.lexer.BeaverTokenType;
import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.common.text.rules.WordDetector;

@SuppressWarnings("nls")
public class BeaverSourceScanner extends RuleBasedScanner
{
	/**
	 * A keyword detector.
	 */
	static class KeywordDetector implements IWordDetector
	{
		private int _index;

		public boolean isWordStart(char c)
		{
			this._index = 0;

			return c == '%';
		}

		public boolean isWordPart(char c)
		{
			this._index++;

			if (this._index == 1)
			{
				return Character.isJavaIdentifierStart(c);
			}
			else
			{
				return Character.isJavaIdentifierPart(c);
			}
		}
	}

	/**
	 * DTDScanner
	 */
	public BeaverSourceScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		rules.add(new SingleLineRule("\"", "\"", createToken(BeaverTokenType.STRING_DOUBLE)));
		rules.add(new SingleLineRule("'", "'", createToken(BeaverTokenType.STRING_SINGLE)));

		WordRule keywordRule = new WordRule(new KeywordDetector(), Token.UNDEFINED);
		keywordRule.addWord("%class", createToken(BeaverTokenType.CLASS));
		keywordRule.addWord("%embed", createToken(BeaverTokenType.EMBED));
		keywordRule.addWord("%goal", createToken(BeaverTokenType.GOAL));
		keywordRule.addWord("%header", createToken(BeaverTokenType.HEADER));
		keywordRule.addWord("%implements", createToken(BeaverTokenType.IMPLEMENTS));
		keywordRule.addWord("%import", createToken(BeaverTokenType.IMPORT));
		keywordRule.addWord("%init", createToken(BeaverTokenType.INIT));
		keywordRule.addWord("%left", createToken(BeaverTokenType.LEFT));
		keywordRule.addWord("%package", createToken(BeaverTokenType.PACKAGE));
		keywordRule.addWord("%right", createToken(BeaverTokenType.RIGHT));
		keywordRule.addWord("%terminals", createToken(BeaverTokenType.TERMINALS));
		keywordRule.addWord("%typeof", createToken(BeaverTokenType.TYPEOF));
		rules.add(keywordRule);

		CharacterMapRule cmRule = new CharacterMapRule();
		cmRule.add(';', createToken(BeaverTokenType.SEMICOLON));
		cmRule.add('=', createToken(BeaverTokenType.EQUAL));
		cmRule.add('|', createToken(BeaverTokenType.PIPE));
		cmRule.add('?', createToken(BeaverTokenType.QUESTION));
		cmRule.add('*', createToken(BeaverTokenType.STAR));
		cmRule.add('+', createToken(BeaverTokenType.PLUS));
		rules.add(cmRule);

		rules.add(new WordRule(new WordDetector(), createToken(BeaverTokenType.IDENTIFIER)));

		this.setRules(rules.toArray(new IRule[rules.size()]));
		// this.setDefaultReturnToken(this.createToken("text"));
	}

	/**
	 * createToken
	 * 
	 * @param string
	 * @return
	 */
	protected IToken createToken(BeaverTokenType type)
	{
		return new Token(type.getScope());
	}
}
