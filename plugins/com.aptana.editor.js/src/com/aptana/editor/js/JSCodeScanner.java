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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.QueuedRuleBasedScanner;
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
public class JSCodeScanner extends QueuedRuleBasedScanner
{
	/*
	 * The last non-whitespace/EOF/undefined token we returned. Used for lookbehinds.
	 */
	private IToken lastToken;

	// In case we're doing lookaheads on the current token, we temporarily hold the current token's offset and length in
	// these vars
	private Integer fOrigOffset;
	private Integer fLength;

	/*
	 * This is a hack to keep track of function declarations so we use special tokens for args and parens. FIXME This
	 * should really be a stack so we handle nested functions.
	 */
	private boolean inFunctionDefinition;

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
		addWordRules(wordRule, createToken(JSScopeType.EVENT_HANDLER_FUNCTION),
				JSLanguageConstants.EVENT_HANDLER_FUNCTIONS);
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
		wordRule.addWord("function", createToken(JSScopeType.FUNCTION_KEYWORD)); //$NON-NLS-1$
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
		cmRule.add('(', createToken(JSScopeType.LEFT_PAREN));
		cmRule.add(')', createToken(JSScopeType.RIGHT_PAREN));
		cmRule.add('[', createToken(JSScopeType.BRACKET));
		cmRule.add(']', createToken(JSScopeType.BRACKET));
		cmRule.add('{', createToken(JSScopeType.CURLY_BRACE));
		cmRule.add('}', createToken(JSScopeType.CURLY_BRACE));
		cmRule.add(',', createToken(JSScopeType.COMMA));
		rules.add(cmRule);

		// Numbers
		rules.add(new JSNumberRule(createToken(JSScopeType.NUMBER)));

		// Periods outside numbers
		cmRule = new CharacterMapRule();
		cmRule.add('.', createToken(JSScopeType.PERIOD));
		rules.add(cmRule);

		// identifiers
		rules.add(new WordRule(new JSIdentifierDetector(), createToken(JSScopeType.SOURCE)));

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	@Override
	public IToken nextToken()
	{
		fOrigOffset = null;
		fLength = null;
		IToken next = super.nextToken();
		// for identifier after 'function' give it special entity function name scope
		if (scopeEquals(lastToken, JSScopeType.FUNCTION_KEYWORD) && scopeEquals(next, JSScopeType.SOURCE))
		{
			next = createToken(JSScopeType.FUNCTION_NAME);
		}
		// Parens outside function definition have a generic scope for both beginning and end...
		else if (scopeEquals(next, JSScopeType.LEFT_PAREN)
				&& !(scopeEquals(lastToken, JSScopeType.FUNCTION_KEYWORD) || scopeEquals(lastToken,
						JSScopeType.FUNCTION_NAME)))
		{
			next = createToken(JSScopeType.PARENTHESIS);
		}
		// ')' should be given generic paren scope when outside function definition
		else if (scopeEquals(next, JSScopeType.RIGHT_PAREN) && !inFunctionDefinition)
		{
			next = createToken(JSScopeType.PARENTHESIS);
		}
		// hold state that we're declaring a function when we see 'function'
		else if (scopeEquals(next, JSScopeType.FUNCTION_KEYWORD))
		{
			inFunctionDefinition = true;
		}
		// get out of function definition when we see '{' or '}'
		else if (inFunctionDefinition && scopeEquals(next, JSScopeType.CURLY_BRACE))
		{
			inFunctionDefinition = false;
		}
		// give function parameters/arguments special scopes
		else if (inFunctionDefinition && scopeEquals(next, JSScopeType.SOURCE))
		{
			next = createToken(JSScopeType.FUNCTION_PARAMETER);
		}

		// HACK Anonymous function name check, look for following "=" and then "function"
		if (!inFunctionDefinition && scopeEquals(next, JSScopeType.SOURCE))
		{
			// Store offset and length since we're going to do lookaheads. But don't assign yet, or it messes up the
			// getToken...() calls below.
			int length = getTokenLength();
			int offset = getTokenOffset();

			// We need to queue up all this stuff at once at the end....
			List<Entry> entries = new ArrayList<QueuedRuleBasedScanner.Entry>();
			IToken ahead = super.nextToken();
			entries.add(new Entry(null, ahead, getTokenOffset(), getTokenLength()));
			// keep going until we hit one of our tokens or EOF
			while (ahead != null && !ahead.isEOF() && !ahead.isOther())
			{
				ahead = super.nextToken();
				entries.add(new Entry(null, ahead, getTokenOffset(), getTokenLength()));
			}
			if (scopeEquals(ahead, JSScopeType.OPERATOR))
			{
				ahead = super.nextToken();
				entries.add(new Entry(null, ahead, getTokenOffset(), getTokenLength()));
				// keep going until we hit one of our tokens or EOF
				while (ahead != null && !ahead.isEOF() && !ahead.isOther())
				{
					ahead = super.nextToken();
					entries.add(new Entry(null, ahead, getTokenOffset(), getTokenLength()));
				}
				if (scopeEquals(ahead, JSScopeType.FUNCTION_KEYWORD))
				{
					next = createToken(JSScopeType.FUNCTION_NAME);
				}
			}
			for (Entry entry : entries)
			{
				queue.add(entry);
			}
			fOrigOffset = offset;
			fLength = length;
		}

		// for lookbacks, only store last non-whitespace token
		if (next.isOther())
		{
			lastToken = next;
		}
		return next;
	}

	private boolean scopeEquals(IToken next, JSScopeType scopeType)
	{
		return scopeEquals(next, scopeType.getScope());
	}

	@Override
	public int getTokenLength()
	{
		// If we did lookaheads, use the right length from before lookahead...
		if (fLength != null)
		{
			try
			{
				return fLength;
			}
			finally
			{
				fLength = null;
			}
		}
		return super.getTokenLength();
	}

	@Override
	public int getTokenOffset()
	{
		// If we did lookaheads, use the right offset from before lookahead...
		if (fOrigOffset != null)
		{
			try
			{
				return fOrigOffset;
			}
			finally
			{
				fOrigOffset = null;
			}
		}
		return super.getTokenOffset();
	}

	private boolean scopeEquals(IToken next, String scope)
	{
		return next != null && next.getData() != null && scope.equals(next.getData());
	}

	@Override
	public void setRange(IDocument document, int offset, int length)
	{
		lastToken = null;
		inFunctionDefinition = false;
		super.setRange(document, offset, length);
	}
}
