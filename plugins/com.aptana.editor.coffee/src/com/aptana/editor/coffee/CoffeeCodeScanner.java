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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.coffee.parsing.lexer.CoffeeScanner;
import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.QueuedRuleBasedScanner;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.js.text.rules.JSIdentifierDetector;
import com.aptana.editor.js.text.rules.JSNumberRule;
import com.aptana.editor.js.text.rules.JSOperatorDetector;
import com.aptana.js.core.JSLanguageConstants;

public class CoffeeCodeScanner extends QueuedRuleBasedScanner
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
	private boolean inClassDecl;
	private boolean inFunctionArgs;

	private static Map<Object, IToken> fgTokenCache = new HashMap<Object, IToken>();

	/**
	 * CoffeeCodeScanner
	 */
	public CoffeeCodeScanner()
	{
		super();
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
		addWordRules(wordRule, createToken(CoffeeScopeType.CONTROL_KEYWORD), CoffeeScanner.JS_KEYWORDS);
		addWordRules(wordRule, createToken(CoffeeScopeType.CONTROL_KEYWORD), CoffeeScanner.COFFEE_KEYWORDS);
		// TODO Break out each word as a separate scope type!
		addWordRules(wordRule, createToken(CoffeeScopeType.TRUE), "true", "yes", "on");
		addWordRules(wordRule, createToken(CoffeeScopeType.FALSE), "false", "no", "off");
		addWordRules(wordRule, createToken(CoffeeScopeType.NULL), "null");
		addWordRules(wordRule, createToken(CoffeeScopeType.LANGUAGE_VARIABLE), "super", "this", "extends");
		addWordRules(wordRule, createToken(CoffeeScopeType.LANGUAGE_CONSTANT), "Infinity", "NaN", "undefined");
		addWordRules(wordRule, createToken(CoffeeScopeType.NEW), "new");
		addWordRules(wordRule, createToken(CoffeeScopeType.EXTENDS), "extends");
		addWordRules(wordRule, createToken(CoffeeScopeType.CLASS), "class");
		rules.add(wordRule);

		// Instance variables
		wordRule = new WordRule(new InstanceVariableDetector(), createToken(CoffeeScopeType.INSTANCE_VARIABLE));
		rules.add(wordRule);

		// FIXME Doesn't seem to be picking this up properly in "range = [1..5]"
		// '..' and '...' - Ranges
		wordRule = new WordRule(new MultiplePeriodsDetector(), Token.UNDEFINED);
		addWordRules(wordRule, createToken(CoffeeScopeType.INCLUSIVE_RANGE), "..");
		addWordRules(wordRule, createToken(CoffeeScopeType.EXCLUSIVE_RANGE), "...");
		rules.add(wordRule);

		// Operators
		// FIXME Properly detect Coffee operators. Just use another set here grabbed from Textmate bundle (rather than
		// JS set)?
		JSOperatorDetector operatorDetector = new JSOperatorDetector();
		operatorDetector.addWord("->");
		operatorDetector.addWord("=>");
		wordRule = new WordRule(operatorDetector, Token.UNDEFINED);
		addWordRules(wordRule, createToken(CoffeeScopeType.OPERATOR), JSLanguageConstants.OPERATORS);
		addWordRules(wordRule, createToken(CoffeeScopeType.FUNC), "->");
		addWordRules(wordRule, createToken(CoffeeScopeType.BOUND_FUNC), "=>");
		rules.add(wordRule);

		// Punctuation
		CharacterMapRule cmRule = new CharacterMapRule();
		cmRule.add(':', createToken(CoffeeScopeType.COLON));
		cmRule.add(';', createToken(CoffeeScopeType.SEMICOLON));
		cmRule.add('(', createToken(CoffeeScopeType.LEFT_PAREN));
		cmRule.add(')', createToken(CoffeeScopeType.RIGHT_PAREN));
		cmRule.add('[', createToken(CoffeeScopeType.LEFT_BRACKET));
		cmRule.add(']', createToken(CoffeeScopeType.RIGHT_BRACKET));
		cmRule.add('{', createToken(CoffeeScopeType.LEFT_CURLY));
		cmRule.add('}', createToken(CoffeeScopeType.RIGHT_CURLY));
		cmRule.add(',', createToken(CoffeeScopeType.COMMA));
		cmRule.add('.', createToken(CoffeeScopeType.PERIOD));
		cmRule.add('=', createToken(CoffeeScopeType.EQUAL));
		rules.add(cmRule);

		// Single character operators...
		CharacterMapRule rule = new CharacterMapRule();
		// FIXME Break out the operators individually?
		for (char operator : JSLanguageConstants.SINGLE_CHARACTER_OPERATORS)
		{
			rule.add(operator, createToken(CoffeeScopeType.OPERATOR));
		}
		rules.add(rule);

		// Numbers
		rules.add(new JSNumberRule(createToken(CoffeeScopeType.NUMERIC)));

		// identifiers
		rules.add(new WordRule(new JSIdentifierDetector(), createToken(CoffeeScopeType.IDENTIFIER)));

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	@Override
	public IToken nextToken()
	{
		fOrigOffset = null;
		fLength = null;
		IToken currentToken = super.nextToken();
		if (currentToken.isEOF())
		{
			return currentToken;
		}

		if (inClassDecl && currentToken.isWhitespace())
		{
			// If we've hit a new line, we're not in class decl anymore!
			if (hasNewline(getTokenOffset(), getTokenLength()))
			{
				inClassDecl = false;
			}
		}

		if (CoffeeScopeType.CLASS == currentToken.getData())
		{
			inClassDecl = true;
		}

		if (lastToken != null && currentToken.isOther())
		{
			if (CoffeeScopeType.NEW == lastToken.getData())
			{
				currentToken = createToken(CoffeeScopeType.ENTITY_TYPE_INSTANCE);
			}
			else if (CoffeeScopeType.EXTENDS == lastToken.getData())
			{
				currentToken = createToken(CoffeeScopeType.SUPERCLASS);
			}
			else if (CoffeeScopeType.CLASS == lastToken.getData())
			{
				currentToken = createToken(CoffeeScopeType.CLASS_NAME);
			}
		}

		// if in function decl, use special scopes for args, make trailing colon an operator.
		if (inFunctionDefinition)
		{
			if (CoffeeScopeType.LEFT_PAREN == currentToken.getData()
					|| CoffeeScopeType.RIGHT_PAREN == currentToken.getData()
					|| CoffeeScopeType.IDENTIFIER == currentToken.getData())
			{
				if (CoffeeScopeType.LEFT_PAREN == currentToken.getData())
				{
					inFunctionArgs = true;
				}
				currentToken = createToken(ICoffeeScopeConstants.PARAMETER_VARIABLE);
			}
			else if (CoffeeScopeType.COLON == currentToken.getData())
			{
				currentToken = createToken(ICoffeeScopeConstants.OPERATOR);
			}
			else if (CoffeeScopeType.FUNC == currentToken.getData()
					|| CoffeeScopeType.BOUND_FUNC == currentToken.getData())
			{
				// name is mis-leading, but we still need the meta.inline scope
				inFunctionArgs = true;
			}
		}
		// Check for assignment...
		else if (CoffeeScopeType.IDENTIFIER == currentToken.getData())
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
			// look to see if this is a function declaration or variable assignment
			if (CoffeeScopeType.EQUAL == ahead.getData() || CoffeeScopeType.COLON == ahead.getData())
			{
				// keep going until we hit ->, =>, newline, or EOF
				while (!ahead.isEOF())
				{
					ahead = super.nextToken();
					entries.add(new Entry(null, ahead, getTokenOffset(), getTokenLength()));
					if (CoffeeScopeType.FUNC == ahead.getData() || CoffeeScopeType.BOUND_FUNC == ahead.getData())
					{
						inFunctionDefinition = true;
						currentToken = createToken(CoffeeScopeType.FUNCTION_NAME);
						break;
					}
					else if (ahead.isWhitespace())
					{
						// If it's newline, stop looking for func sigils!
						Entry lastEntry = entries.get(entries.size() - 1);
						if (hasNewline(lastEntry.getTokenOffset(), lastEntry.getTokenLength()))
						{
							break;
						}
					}
				}
				if (!inFunctionDefinition)
				{
					currentToken = createToken(ICoffeeScopeConstants.ASSIGNMENT_VARIABLE);
				}
			}

			// We need to push these tokens back to beginning of the queue!
			Queue<QueuedRuleBasedScanner.Entry> newQueue = new LinkedList<QueuedRuleBasedScanner.Entry>(entries);
			newQueue.addAll(entries);
			newQueue.addAll(queue);
			queue.clear();
			queue.addAll(newQueue);

			fOrigOffset = offset;
			fLength = length;
		}
		// Check for anonymous function
		else if (CoffeeScopeType.LEFT_PAREN == currentToken.getData())
		{
			// Store offset and length since we're going to do lookaheads. But don't assign yet, or it messes up the
			// getToken...() calls below.
			int length = getTokenLength();
			int offset = getTokenOffset();

			// We need to queue up all this stuff at once at the end....
			List<Entry> entries = new ArrayList<QueuedRuleBasedScanner.Entry>();
			IToken ahead = super.nextToken();
			entries.add(new Entry(null, ahead, getTokenOffset(), getTokenLength()));

			// keep going until we hit ->, =>, newline, or EOF
			while (!ahead.isEOF())
			{
				ahead = super.nextToken();
				entries.add(new Entry(null, ahead, getTokenOffset(), getTokenLength()));
				if (CoffeeScopeType.FUNC == ahead.getData() || CoffeeScopeType.BOUND_FUNC == ahead.getData())
				{
					inFunctionArgs = true;
					inFunctionDefinition = true;
					currentToken = createToken(ICoffeeScopeConstants.PARAMETER_VARIABLE);
					break;
				}
				else if (ahead.isWhitespace())
				{
					// If it's newline, stop looking for func sigils!
					Entry lastEntry = entries.get(entries.size() - 1);
					if (hasNewline(lastEntry.getTokenOffset(), lastEntry.getTokenLength()))
					{
						break;
					}
				}
			}

			// We need to push these tokens back to beginning of the queue!
			Queue<QueuedRuleBasedScanner.Entry> newQueue = new LinkedList<QueuedRuleBasedScanner.Entry>(entries);
			newQueue.addAll(entries);
			newQueue.addAll(queue);
			queue.clear();
			queue.addAll(newQueue);

			fOrigOffset = offset;
			fLength = length;
		}

		// Prepend meta scopes
		StringBuilder builder = new StringBuilder();
		if (inClassDecl)
		{
			builder.append(ICoffeeScopeConstants.META_CLASS).append(' ');
		}
		if (inFunctionArgs)
		{
			builder.append(ICoffeeScopeConstants.META_INLINE_FUNCTION).append(' ');
		}
		if (currentToken.getData() != null)
		{
			builder.append(currentToken.getData().toString());
		}

		// if we hit superclass name, we're not inside class decl anymore
		if (CoffeeScopeType.SUPERCLASS == currentToken.getData() && inClassDecl)
		{
			inClassDecl = false;
		}
		// escape from function decl/args when we hit func sigil
		else if (inFunctionDefinition
				&& (CoffeeScopeType.BOUND_FUNC == currentToken.getData() || CoffeeScopeType.FUNC == currentToken
						.getData()))
		{
			inFunctionDefinition = false;
			inFunctionArgs = false;
		}

		// save last non-whitespace token for lookbacks
		if (currentToken.isOther())
		{
			lastToken = currentToken;
		}

		// If we have built a scope with metas, or we have a token whose data may still be a CoffeeScopeType instead of
		// a String...
		if (builder.length() > 0 || currentToken.isOther())
		{
			return createToken(builder.toString().trim());
		}
		return currentToken;
	}

	private boolean hasNewline(int tokenOffset, int tokenLength)
	{
		String[] lineDelims = fDocument.getLegalLineDelimiters();
		try
		{
			String src = fDocument.get(tokenOffset, tokenLength);
			for (String lineDelim : lineDelims)
			{
				if (src.contains(lineDelim))
				{
					return true;
				}
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}
		return false;
	}

	@Override
	public void setRange(IDocument document, int offset, int length)
	{
		this.lastToken = null;
		this.inFunctionDefinition = false;
		this.inClassDecl = false;
		this.inFunctionArgs = false;
		this.fOrigOffset = null;
		this.fLength = null;
		super.setRange(document, offset, length);
	}

	protected synchronized IToken createToken(Object obj)
	{
		if (!fgTokenCache.containsKey(obj))
		{
			fgTokenCache.put(obj, new Token(obj));
		}
		return fgTokenCache.get(obj);
	}

	@Override
	public int getTokenLength()
	{
		// If we did lookaheads, use the right length from before lookahead...
		if (fLength != null)
		{
			return fLength;
		}
		return super.getTokenLength();
	}

	@Override
	public int getTokenOffset()
	{
		// If we did lookaheads, use the right offset from before lookahead...
		if (fOrigOffset != null)
		{
			return fOrigOffset;
		}
		return super.getTokenOffset();
	}

	private static class InstanceVariableDetector implements IWordDetector
	{

		public boolean isWordStart(char c)
		{
			return c == '@';
		}

		public boolean isWordPart(char c)
		{
			return Character.isLetterOrDigit(c) || c == '_' || c == '$';
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
