/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
		keywordRule.addWord("%nonassoc", createToken(BeaverTokenType.NONASSOC));
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
