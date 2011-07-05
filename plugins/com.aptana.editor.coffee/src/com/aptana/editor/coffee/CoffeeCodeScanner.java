/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.coffee.parsing.lexer.CoffeeScanner;
import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.js.JSLanguageConstants;
import com.aptana.editor.js.text.rules.JSIdentifierDetector;
import com.aptana.editor.js.text.rules.JSNumberRule;
import com.aptana.editor.js.text.rules.JSOperatorDetector;

public class CoffeeCodeScanner extends RuleBasedScanner
{

	private IToken lastToken;

	/**
	 * CoffeeCodeScanner
	 */
	public CoffeeCodeScanner()
	{
		initRules();
	}

	protected void addWordRules(WordRule wordRule, IToken keywordOperators, Collection<String> words)
	{
		addWordRules(wordRule, keywordOperators, words.toArray(new String[words.size()]));
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
	 * initRules
	 */
	@SuppressWarnings("nls")
	protected void initRules()
	{
		// Please note that ordering of rules is important! the last word rule will end up assigning a token type to any
		// words that don't match the list. So we'll only have non-word source left to match against (like braces or
		// numbers)
		// Also, we try to make the fastest rules run first rather than have slow regexp rules continually getting
		// called. We want them called the least so we should try all faster rules first.
		List<IRule> rules = new ArrayList<IRule>();

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// Keywords
		WordRule wordRule = new WordRule(new JSIdentifierDetector(), Token.UNDEFINED);
		// TODO Just use another set here grabbed from Textmate bundle, rather than re-use the lexer set?
		addWordRules(wordRule, createToken(ICoffeeScopeConstants.CONTROL_KEYWORD), CoffeeScanner.JS_KEYWORDS);
		addWordRules(wordRule, createToken(ICoffeeScopeConstants.CONTROL_KEYWORD), CoffeeScanner.COFFEE_KEYWORDS);
		addWordRules(wordRule, createToken(ICoffeeScopeConstants.TRUE), "true", "yes", "on");
		addWordRules(wordRule, createToken(ICoffeeScopeConstants.FALSE), "false", "no", "off");
		addWordRules(wordRule, createToken(ICoffeeScopeConstants.NULL), "null");
		addWordRules(wordRule, createToken(ICoffeeScopeConstants.LANGUAGE_VARIABLE), "super", "this", "extends");
		addWordRules(wordRule, createToken(ICoffeeScopeConstants.LANGUAGE_CONSTANT), "Infinity", "NaN", "undefined");
		addWordRules(wordRule, createToken(ICoffeeScopeConstants.KEYWORD_NEW), "new");
		addWordRules(wordRule, createToken(ICoffeeScopeConstants.KEYWORD_EXTENDS), "extends");
		addWordRules(wordRule, createToken(ICoffeeScopeConstants.KEYWORD_CLASS), "class");
		rules.add(wordRule);

		// Instance variables
		wordRule = new WordRule(new InstanceVariableDetector(), createToken(ICoffeeScopeConstants.INSTANCE_VARIABLE));
		rules.add(wordRule);

		// FIXME Doesn't seem to be picking this up properly in "range = [1..5]"
		// '..' and '...' - Ranges
		wordRule = new WordRule(new MultiplePeriodsDetector(), Token.UNDEFINED);
		addWordRules(wordRule, createToken(ICoffeeScopeConstants.PERIOD), "..", "...");
		rules.add(wordRule);

		// Operators
		// FIXME Properly detect Coffee operators. Just use another set here grabbed from Textmate bundle (rather than
		// JS set)?
		wordRule = new WordRule(new JSOperatorDetector(), Token.UNDEFINED);
		addWordRules(wordRule, createToken(ICoffeeScopeConstants.OPERATOR), JSLanguageConstants.OPERATORS);
		addWordRules(wordRule, createToken(ICoffeeScopeConstants.FUNCTION_STORAGE), "->", "=>");
		rules.add(wordRule);

		CharacterMapRule rule = new CharacterMapRule();
		for (char operator : JSLanguageConstants.SINGLE_CHARACTER_OPERATORS)
		{
			rule.add(operator, createToken(ICoffeeScopeConstants.OPERATOR));
		}
		rules.add(rule);

		// Punctuation
		CharacterMapRule cmRule = new CharacterMapRule();
		cmRule.add(':', createToken(ICoffeeScopeConstants.COLON));
		cmRule.add(';', createToken(ICoffeeScopeConstants.SEMICOLON));
		cmRule.add('(', createToken(ICoffeeScopeConstants.PAREN));
		cmRule.add(')', createToken(ICoffeeScopeConstants.PAREN));
		cmRule.add('[', createToken(ICoffeeScopeConstants.BRACKET));
		cmRule.add(']', createToken(ICoffeeScopeConstants.BRACKET));
		cmRule.add('{', createToken(ICoffeeScopeConstants.CURLY));
		cmRule.add('}', createToken(ICoffeeScopeConstants.CURLY));
		cmRule.add(',', createToken(ICoffeeScopeConstants.COMMA));
		cmRule.add('.', createToken(ICoffeeScopeConstants.PERIOD));
		rules.add(cmRule);

		// Numbers
		rules.add(new JSNumberRule(createToken(ICoffeeScopeConstants.NUMERIC)));

		// identifiers
		rules.add(new WordRule(new JSIdentifierDetector(), createToken("")));

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	// FIXME Handle detecting function names properly! ICoffeeScopeConstants.FUNCTION_NAME
	// Probably need to just look for identifier token, and then look ahead to see if we see trailing ':' or '=', then
	// '(', stuff, ')' and finally '->' or '=>'

	@Override
	public IToken nextToken()
	{
		IToken next = super.nextToken();
		if (next.isEOF() || next.isWhitespace())
		{
			return next;
		}
		if (lastToken != null && lastToken.isOther())
		{
			if (/*
				 * ICoffeeScopeConstants.IDENTIFIER.equals(lastToken.getData()) &&
				 */ICoffeeScopeConstants.KEYWORD_NEW.equals(lastToken.getData()))
			{
				next = createToken(ICoffeeScopeConstants.ENTITY_TYPE_INSTANCE);
			}
			else if (/*
					 * ICoffeeScopeConstants.IDENTIFIER.equals(lastToken.getData()) &&
					 */ICoffeeScopeConstants.KEYWORD_EXTENDS.equals(lastToken.getData()))
			{
				next = createToken(ICoffeeScopeConstants.SUPERCLASS);
			}
			else if (/*
					 * ICoffeeScopeConstants.IDENTIFIER.equals(lastToken.getData()) &&
					 */ICoffeeScopeConstants.KEYWORD_CLASS.equals(lastToken.getData()))
			{
				next = createToken(ICoffeeScopeConstants.CLASS_NAME);
			}
		}

		lastToken = next;
		return next;
	}

	@Override
	public void setRange(IDocument document, int offset, int length)
	{
		this.lastToken = null;
		super.setRange(document, offset, length);
	}

	protected IToken createToken(String string)
	{
		return new Token(string);
	}

	private static class InstanceVariableDetector implements IWordDetector
	{

		public boolean isWordStart(char c)
		{
			return c == '@';
		}

		public boolean isWordPart(char c)
		{
			return Character.isLetter(c) || c == '_' || c == '$';
		}
	}

	private static class MultiplePeriodsDetector implements IWordDetector
	{

		public boolean isWordStart(char c)
		{
			return c == '.';
		}

		public boolean isWordPart(char c)
		{
			return isWordStart(c);
		}
	}
}
