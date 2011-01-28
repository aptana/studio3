/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.js.parsing.lexer.JSScopeType;
import com.aptana.editor.js.text.rules.JSFunctionCallDetector;
import com.aptana.editor.js.text.rules.JSIdentifierDetector;
import com.aptana.editor.js.text.rules.JSNumberRule;
import com.aptana.editor.js.text.rules.JSOperatorDetector;

/**
 * @author Michael Xia
 * @author Kevin Lindsey
 * @author cwilliams
 */
public class JSCodeScanner extends RuleBasedScanner
{
	// No opts: 3208ms avg
	// Regexp opt: 2476ms avg
	// Regexp + special char opt: 2295ms avg
	// regexp + special char + word: 160-190ms avg (biggest gain was converting operators from regexp to words)

	/**
	 * CodeScanner
	 */
	public JSCodeScanner()
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
	protected IToken createToken(JSScopeType type)
	{
		return new Token(type.getScope());
	}

	/**
	 * initRules
	 */
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

		// Converted word rules
		WordRule wordRule = new WordRule(new JSIdentifierDetector(), Token.UNDEFINED);
		addWordRules(wordRule, createToken(JSScopeType.KEYWORD), JSLanguageConstants.KEYWORD_OPERATORS);
		rules.add(wordRule);

		// FIXME These rules shouldn't actually match the leading period, but we have no way to capture just the rest as
		// the token
		// Functions where we need period to begin it
		wordRule = new WordRule(new JSFunctionCallDetector(), Token.UNDEFINED);
		addWordRules(wordRule, createToken(JSScopeType.SUPPORT_FUNCTION), JSLanguageConstants.SUPPORT_FUNCTIONS);
		addWordRules(wordRule, createToken(JSScopeType.EVENT_HANDLER_FUNCTION), JSLanguageConstants.EVENT_HANDLER_FUNCTIONS);
		addWordRules(wordRule, createToken(JSScopeType.DOM_FUNCTION), JSLanguageConstants.DOM_FUNCTIONS);
		addWordRules(wordRule, createToken(JSScopeType.FIREBUG_FUNCTION), JSLanguageConstants.FIREBUG_FUNCTIONS);
		addWordRules(wordRule, createToken(JSScopeType.DOM_CONSTANTS), JSLanguageConstants.DOM_CONSTANTS);
		addWordRules(wordRule, createToken(JSScopeType.SUPPORT_CONSTANT), JSLanguageConstants.SUPPORT_CONSTANTS);
		rules.add(wordRule);

		// Operators
		wordRule = new WordRule(new JSOperatorDetector(), Token.UNDEFINED);
		addWordRules(wordRule, createToken(JSScopeType.OPERATOR), JSLanguageConstants.OPERATORS);
		rules.add(wordRule);

		CharacterMapRule rule = new CharacterMapRule();
		for (char operator : JSLanguageConstants.SINGLE_CHARACTER_OPERATORS)
		{
			rule.add(operator, createToken(JSScopeType.KEYWORD));
		}
		rules.add(rule);

		// Add word rule for keywords, types, and constants.
		wordRule = new WordRule(new JSIdentifierDetector(), createToken(JSScopeType.SOURCE));
		addWordRules(wordRule, createToken(JSScopeType.CONTROL_KEYWORD), JSLanguageConstants.KEYWORD_CONTROL);
		addWordRules(wordRule, createToken(JSScopeType.CONTROL_KEYWORD), JSLanguageConstants.KEYWORD_CONTROL_FUTURE);
		addWordRules(wordRule, createToken(JSScopeType.STORAGE_TYPE), JSLanguageConstants.STORAGE_TYPES);
		addWordRules(wordRule, createToken(JSScopeType.STORAGE_MODIFIER), JSLanguageConstants.STORAGE_MODIFIERS);
		addWordRules(wordRule, createToken(JSScopeType.SUPPORT_CLASS), JSLanguageConstants.SUPPORT_CLASSES);
		addWordRules(wordRule, createToken(JSScopeType.SUPPORT_DOM_CONSTANT), JSLanguageConstants.SUPPORT_DOM_CONSTANTS);
		wordRule.addWord("true", createToken(JSScopeType.TRUE)); //$NON-NLS-1$
		wordRule.addWord("false", createToken(JSScopeType.FALSE)); //$NON-NLS-1$
		wordRule.addWord("null", createToken(JSScopeType.NULL)); //$NON-NLS-1$
		wordRule.addWord("Infinity", createToken(JSScopeType.CONSTANT)); //$NON-NLS-1$
		wordRule.addWord("NaN", createToken(JSScopeType.CONSTANT)); //$NON-NLS-1$
		wordRule.addWord("undefined", createToken(JSScopeType.CONSTANT)); //$NON-NLS-1$
		wordRule.addWord("super", createToken(JSScopeType.VARIABLE)); //$NON-NLS-1$
		wordRule.addWord("this", createToken(JSScopeType.VARIABLE)); //$NON-NLS-1$
		wordRule.addWord("debugger", createToken(JSScopeType.OTHER_KEYWORD)); //$NON-NLS-1$
		rules.add(wordRule);

		// Punctuation
		CharacterMapRule cmRule = new CharacterMapRule();
		cmRule.add(';', createToken(JSScopeType.SEMICOLON));
		cmRule.add('(', createToken(JSScopeType.PARENTHESIS));
		cmRule.add(')', createToken(JSScopeType.PARENTHESIS));
		cmRule.add('[', createToken(JSScopeType.BRACKET));
		cmRule.add(']', createToken(JSScopeType.BRACKET));
		cmRule.add('{', createToken(JSScopeType.CURLY_BRACE));
		cmRule.add('}', createToken(JSScopeType.CURLY_BRACE));
		cmRule.add(',', createToken(JSScopeType.COMMA));
		rules.add(cmRule);

		// Numbers
		rules.add(new JSNumberRule(createToken(JSScopeType.NUMBER)));

		// identifiers
		rules.add(new WordRule(new JSIdentifierDetector(), createToken(JSScopeType.SOURCE)));

		setRules(rules.toArray(new IRule[rules.size()]));
	}
}
