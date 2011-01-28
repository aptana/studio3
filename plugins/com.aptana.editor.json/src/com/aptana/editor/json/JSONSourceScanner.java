/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.json.parsing.lexer.JSONTokenType;
import com.aptana.editor.json.text.rules.JSONNumberRule;
import com.aptana.editor.json.text.rules.JSONPropertyRule;

@SuppressWarnings("nls")
public class JSONSourceScanner extends RuleBasedScanner
{
	public class KeywordDetector implements IWordDetector
	{
		public boolean isWordPart(char c)
		{
			return isWordStart(c);
		}

		public boolean isWordStart(char c)
		{
			return Character.isLetter(c);
		}
	}

	/**
	 * JSONSourceScanner
	 */
	public JSONSourceScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		rules.add(new EndOfLineRule("//", createToken(JSONTokenType.COMMENT)));
		rules.add(new MultiLineRule("/*", "*/", createToken(JSONTokenType.COMMENT)));

		// NOTE: This case is covered during partitioning, but we need this for the parser
		rules.add( //
			new JSONPropertyRule( //
				createToken(JSONTokenType.STRING_SINGLE), //
				createToken(JSONTokenType.STRING_DOUBLE), //
				createToken(JSONTokenType.PROPERTY) //
			) //
		);

		WordRule keywordRule = new WordRule(new KeywordDetector(), Token.UNDEFINED);
		keywordRule.addWord("true", createToken(JSONTokenType.TRUE));
		keywordRule.addWord("false", createToken(JSONTokenType.FALSE));
		keywordRule.addWord("null", createToken(JSONTokenType.NULL));
		rules.add(keywordRule);

		CharacterMapRule cmRule = new CharacterMapRule();
		cmRule.add('{', createToken(JSONTokenType.LCURLY));
		cmRule.add('}', createToken(JSONTokenType.RCURLY));
		cmRule.add('[', createToken(JSONTokenType.LBRACKET));
		cmRule.add(']', createToken(JSONTokenType.RBRACKET));
		cmRule.add(',', createToken(JSONTokenType.COMMA));
		cmRule.add(':', createToken(JSONTokenType.COLON));
		rules.add(cmRule);

		// Numbers
		rules.add(new JSONNumberRule(createToken(JSONTokenType.NUMBER)));

		this.setRules(rules.toArray(new IRule[rules.size()]));
		// this.setDefaultReturnToken(this.createToken("text"));
	}

	/**
	 * createToken
	 * 
	 * @param string
	 * @return
	 */
	protected IToken createToken(JSONTokenType type)
	{
		return new Token(type.getScope());
	}
}
