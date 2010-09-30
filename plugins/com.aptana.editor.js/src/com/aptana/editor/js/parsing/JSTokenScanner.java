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
package com.aptana.editor.js.parsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.common.text.rules.WordDetector;
import com.aptana.editor.js.IJSTokenScanner;
import com.aptana.editor.js.JSLanguageConstants;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.editor.js.text.rules.CharacterMapRule;
import com.aptana.editor.js.text.rules.JSIdentifierDetector;
import com.aptana.editor.js.text.rules.JSNumberRule;
import com.aptana.editor.js.text.rules.JSRegExpRule;
import com.aptana.editor.js.text.rules.JSOperatorDetector;

/**
 * @author Michael Xia
 * @author Kevin Lindsey
 * @author cwilliams
 */
public class JSTokenScanner extends RuleBasedScanner implements IJSTokenScanner
{
	private static String VAR_CONST = "const"; //$NON-NLS-1$

	private IToken fToken;

	/**
	 * JSTokenScanner
	 */
	public JSTokenScanner()
	{
		initRules();
	}

	/**
	 * addWordRules
	 * 
	 * @param wordRule
	 * @param keywordOperators
	 * @param words
	 */
	protected void addWordRules(WordRule wordRule, IToken keywordOperators, String... words)
	{
		for (String word : words)
		{
			wordRule.addWord(word, keywordOperators);
		}
	}

	/**
	 * createToken
	 * 
	 * @param type
	 * @return
	 */
	protected IToken createToken(JSTokenType type)
	{
		return new Token(type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.IJSTokenScanner#hasDivisionStart()
	 */
	public boolean hasDivisionStart()
	{
		if (fToken == null || fToken.getData() == null)
		{
			return false;
		}

		JSTokenType tokenType = (JSTokenType) fToken.getData();
		switch (tokenType)
		{
			case IDENTIFIER:
			case NUMBER:
			case REGEX:
			case STRING:
			case RPAREN:
			case PLUS_PLUS:
			case MINUS_MINUS:
			case RBRACKET:
			case RCURLY:
			case FALSE:
			case NULL:
			case THIS:
			case TRUE:
				return true;
		}
		return false;
	}

	/**
	 * initRules
	 */
	protected void initRules()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// generic whitespace rule
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// comments and documentation
		rules.add(new EndOfLineRule("///", createToken(JSTokenType.VSDOC))); //$NON-NLS-1$
		rules.add(new EndOfLineRule("//", createToken(JSTokenType.SINGLELINE_COMMENT))); //$NON-NLS-1$
		rules.add(new MultiLineRule("/**", "*/", createToken(JSTokenType.SDOC), (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("/*", "*/", createToken(JSTokenType.MULTILINE_COMMENT))); //$NON-NLS-1$ //$NON-NLS-2$

		// quoted strings
		IToken token = createToken(JSTokenType.STRING);
		rules.add(new SingleLineRule("\"", "\"", token, '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new SingleLineRule("\'", "\'", token, '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// regex
		rules.add(new JSRegExpRule(createToken(JSTokenType.REGEX)));

		WordRule wordRule = new WordRule(new JSIdentifierDetector(), Token.UNDEFINED);
		for (String keyword : JSLanguageConstants.KEYWORD_OPERATORS)
		{
			JSTokenType type = JSTokenType.get(keyword);

			wordRule.addWord(keyword, createToken(type));
		}
		addWordRules(wordRule, createToken(JSTokenType.IDENTIFIER), JSLanguageConstants.SUPPORT_FUNCTIONS);
		addWordRules(wordRule, createToken(JSTokenType.IDENTIFIER), JSLanguageConstants.EVENT_HANDLER_FUNCTIONS);
		addWordRules(wordRule, createToken(JSTokenType.IDENTIFIER), JSLanguageConstants.DOM_FUNCTIONS);
		rules.add(wordRule);

		// operators
		wordRule = new WordRule(new JSOperatorDetector(), Token.UNDEFINED);
		for (String operator : JSLanguageConstants.OPERATORS)
		{
			JSTokenType type = JSTokenType.get(operator);

			wordRule.addWord(operator, createToken(type));
		}
		rules.add(wordRule);
		
		// NOTE: Numbers can start with a period, so we need to check for numbers
		// before the operator list below, which includes the dot operator
		rules.add(new JSNumberRule(createToken(JSTokenType.NUMBER)));

		// single-character operators and punctuation
		CharacterMapRule cmRule = new CharacterMapRule();
		for (char operator : JSLanguageConstants.SINGLE_CHARACTER_OPERATORS)
		{
			JSTokenType type = JSTokenType.get(Character.toString(operator));

			cmRule.add(operator, createToken(type));
		}
		cmRule.add(';', createToken(JSTokenType.SEMICOLON));
		cmRule.add('(', createToken(JSTokenType.LPAREN));
		cmRule.add(')', createToken(JSTokenType.RPAREN));
		cmRule.add('[', createToken(JSTokenType.LBRACKET));
		cmRule.add(']', createToken(JSTokenType.RBRACKET));
		cmRule.add('{', createToken(JSTokenType.LCURLY));
		cmRule.add('}', createToken(JSTokenType.RCURLY));
		cmRule.add(',', createToken(JSTokenType.COMMA));
		cmRule.add(':', createToken(JSTokenType.COLON));
		cmRule.add('.', createToken(JSTokenType.DOT));
		cmRule.add('?', createToken(JSTokenType.QUESTION));
		rules.add(cmRule);

		// other keywords, types, and constants
		wordRule = new WordRule(new WordDetector(), Token.UNDEFINED);
		for (String keyword : JSLanguageConstants.KEYWORD_CONTROL)
		{
			JSTokenType type = JSTokenType.get(keyword);

			wordRule.addWord(keyword, createToken(type));
		}
		for (String keyword : JSLanguageConstants.GRAMMAR_KEYWORDS)
		{
			JSTokenType type = JSTokenType.get(keyword);

			wordRule.addWord(keyword, createToken(type));
		}
		wordRule.addWord(VAR_CONST, createToken(JSTokenType.VAR));
		addWordRules(wordRule, createToken(JSTokenType.IDENTIFIER), JSLanguageConstants.SUPPORT_CLASSES);
		addWordRules(wordRule, createToken(JSTokenType.IDENTIFIER), JSLanguageConstants.SUPPORT_DOM_CONSTANTS);
		rules.add(wordRule);

		// identifiers
		rules.add(new WordRule(new JSIdentifierDetector(), createToken(JSTokenType.IDENTIFIER)));

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#nextToken()
	 */
	@Override
	public IToken nextToken()
	{
		return fToken = super.nextToken();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#setRange(org.eclipse.jface.text.IDocument, int, int)
	 */
	@Override
	public void setRange(final IDocument document, int offset, int length)
	{
		fToken = null;

		super.setRange(document, offset, length);
	}
}
