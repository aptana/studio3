/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.common.text.rules.WordDetector;
import com.aptana.editor.js.IJSTokenScanner;
import com.aptana.editor.js.JSLanguageConstants;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.editor.js.text.rules.JSIdentifierDetector;
import com.aptana.editor.js.text.rules.JSNumberRule;
import com.aptana.editor.js.text.rules.JSOperatorDetector;
import com.aptana.editor.js.text.rules.JSRegExpRule;

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

		// NOTE: We have to try to match vsdoc comments before single-line comments because "//" will match "///"
		// resulting in all vsdoc comments being treated as single-line comments
		rules.add(new EndOfLineRule("///", createToken(JSTokenType.VSDOC))); //$NON-NLS-1$
		rules.add(new EndOfLineRule("//", createToken(JSTokenType.SINGLELINE_COMMENT))); //$NON-NLS-1$

		// NOTE: For some reason the sdoc rule matches "/**/". We have to match sdoc before multi-line comments, so we
		// add a special rule to catch this one case where multi-line needs to match before sdoc
		// @formatter:off
		WordRule commentWordRule = new WordRule(
			new IWordDetector()
			{
				private boolean _closed;

				public boolean isWordStart(char c)
				{
					this._closed = false;

					return c == '/';
				}

				public boolean isWordPart(char c)
				{
					boolean result = false;

					if (this._closed == false)
					{
						if (c == '/')
						{
							this._closed = true;
							result = true;
						}
						else if (c == '*')
						{
							result = true;
						}
					}

					return result;
				}
			},
			Token.UNDEFINED
		);
		// @formatter:on
		commentWordRule.addWord("/**/", createToken(JSTokenType.MULTILINE_COMMENT));
		rules.add(commentWordRule);

		// NOTE: We have to try to match sdoc comments before multi-line comments because "/*" will match "/**"
		// resulting in all sdoc comments being treated as multi-line comments
		rules.add(new MultiLineRule("/**", "*/", createToken(JSTokenType.SDOC), '\0', true)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("/*", "*/", createToken(JSTokenType.MULTILINE_COMMENT), '\0', true)); //$NON-NLS-1$ //$NON-NLS-2$

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
		IToken result = super.nextToken();

		// NOTE: Only save the last token if it is not whitespace
		if (result.isWhitespace() == false)
		{
			fToken = result;
		}

		return result;
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
