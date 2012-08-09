/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import beaver.Symbol;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.js.parsing.JSFlexScanner;
import com.aptana.editor.js.parsing.Terminals;
import com.aptana.editor.js.parsing.lexer.JSScopeType;

/**
 * Code scanner for Javascript based on JFlex.
 */
public class JSCodeScanner implements ITokenScanner
{
	private static final Token UNDEFINED_TOKEN = new Token(JSScopeType.UNDEFINED.getScope());
	private static final Token NUMBER_TOKEN = new Token(JSScopeType.NUMBER.getScope());
	private static final Token IDENTIFIER_TOKEN = new Token(JSScopeType.SOURCE.getScope());
	private static final Token SEMICOLON_TOKEN = new Token(JSScopeType.SEMICOLON.getScope());
	private static final Token OPERATOR_TOKEN = new Token(JSScopeType.OPERATOR.getScope());
	private static final Token STORAGE_TOKEN = new Token(JSScopeType.STORAGE_TYPE.getScope());
	private static final Token STORAGE_MODIFIER_TOKEN = new Token(JSScopeType.STORAGE_MODIFIER.getScope());
	private static final Token TRUE_TOKEN = new Token(JSScopeType.TRUE.getScope());
	private static final Token FALSE_TOKEN = new Token(JSScopeType.FALSE.getScope());
	private static final Token NULL_TOKEN = new Token(JSScopeType.NULL.getScope());
	private static final Token CONSTANT_TOKEN = new Token(JSScopeType.CONSTANT.getScope());
	private static final Token VARIABLE_TOKEN = new Token(JSScopeType.VARIABLE.getScope());
	private static final Token OTHER_KEYWORD_TOKEN = new Token(JSScopeType.OTHER_KEYWORD.getScope());
	private static final Token PARENTHESIS_TOKEN = new Token(JSScopeType.PARENTHESIS.getScope());
	private static final Token BRACKET_TOKEN = new Token(JSScopeType.BRACKET.getScope());
	private static final Token CURLY_BRACE_TOKEN = new Token(JSScopeType.CURLY_BRACE.getScope());
	private static final Token COMMA_TOKEN = new Token(JSScopeType.COMMA.getScope());
	private static final Token CONTROL_KEYWORD_TOKEN = new Token(JSScopeType.CONTROL_KEYWORD.getScope());
	private static final Token SUPPORT_CLASS_TOKEN = new Token(JSScopeType.SUPPORT_CLASS.getScope());
	private static final Token SUPPORT_DOM_CONSTANT_TOKEN = new Token(JSScopeType.SUPPORT_DOM_CONSTANT.getScope());
	private static final Token PERIOD_TOKEN = new Token(JSScopeType.PERIOD.getScope());
	private static final Token KEYWORD_OPERATOR_TOKEN = new Token(JSScopeType.KEYWORD.getScope());
	private static final Token FIREBUG_FUNCTION_TOKEN = new Token(JSScopeType.FIREBUG_FUNCTION.getScope());

	private static final Token FUNCTION_TOKEN = new Token(JSScopeType.FUNCTION_KEYWORD.getScope());
	private static final Token FUNCTION_NAME_TOKEN = new Token(JSScopeType.FUNCTION_NAME.getScope());
	private static final Token FUNCTION_PARAMETER_TOKEN = new Token(JSScopeType.FUNCTION_PARAMETER.getScope());
	private static final Token FUNCTION_LPAREN_TOKEN = new Token(JSScopeType.LEFT_PAREN.getScope());
	private static final Token FUNCTION_RPAREN_TOKEN = new Token(JSScopeType.RIGHT_PAREN.getScope());

	// Note: replicating behavior old rule-based scanner, but this should probably be source token?
	private static final Token COLON_TOKEN = new Token(null);

	private static final Set<String> CONSTANTS = new HashSet<String>();
	private static final Set<String> VARIABLES = new HashSet<String>();
	private static final Set<String> OTHER_KEYWORDS = new HashSet<String>();
	private static final Set<String> SUPPORT_CLASSES = new HashSet<String>();
	private static final Set<String> FIREBUG_FUNCTONS = new HashSet<String>();
	private static final Set<String> KEYWORD_CONTROL_FUTURE = new HashSet<String>();
	private static final Set<String> STORAGE_TYPES = new HashSet<String>();
	private static final Set<String> STORAGE_MODIFEERS = new HashSet<String>();
	private static final Set<String> SUPPORT_DOM_CONSTANT = new HashSet<String>();

	// Fill our constant sets.
	static
	{
		CONSTANTS.add("Infinity"); //$NON-NLS-1$
		CONSTANTS.add("NaN"); //$NON-NLS-1$
		CONSTANTS.add("undefined"); //$NON-NLS-1$

		VARIABLES.add("super"); //$NON-NLS-1$
		VARIABLES.add("this"); //$NON-NLS-1$

		OTHER_KEYWORDS.add("debugger"); //$NON-NLS-1$

		FIREBUG_FUNCTONS.add("log"); //$NON-NLS-1$

		for (String s : JSLanguageConstants.SUPPORT_CLASSES)
		{
			SUPPORT_CLASSES.add(s);
		}

		for (String s : JSLanguageConstants.KEYWORD_CONTROL_FUTURE)
		{
			KEYWORD_CONTROL_FUTURE.add(s);
		}
		for (String s : JSLanguageConstants.STORAGE_TYPES)
		{
			STORAGE_TYPES.add(s);
		}
		for (String s : JSLanguageConstants.STORAGE_MODIFIERS)
		{
			STORAGE_MODIFEERS.add(s);
		}
		for (String s : JSLanguageConstants.SUPPORT_DOM_CONSTANTS)
		{
			SUPPORT_DOM_CONSTANT.add(s);
		}
	}

	private enum State
	{
		DEFAULT, FUNCTION_DECLARATION, FUNCTION_DECLARATION_INSIDE_PARENS,
	}

	/**
	 * Used to know in which state we're in (i.e.: detect function).
	 */
	private State fCurrentState = State.DEFAULT;

	/**
	 * JFlex-based scanner.
	 */
	private final JSFlexScanner fScanner = new JSFlexScanner();

	/**
	 * Queue used to put symbols we look-ahead
	 */
	private Queue<Symbol> fQueue = new LinkedList<Symbol>();

	/**
	 * Offset set (needed to properly return the ranges as our offset will be relative to this position).
	 */
	private int fOffset;

	/**
	 * Start with -1 offset so that whitespace token emulation works when document starts with spaces.
	 */
	private Symbol fLastSymbol = new Symbol((short) -1, -1, -1);

	/**
	 * Offset of the token found (relative to fOffset).
	 */
	private int fTokenOffset;

	/**
	 * Length of the token found.
	 */
	private int fTokenLen;

	/**
	 * Whether the last returned token was a 'generated' whitespace token.
	 */
	private boolean fLastWasWhitespace = false;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#setRange(org.eclipse.jface.text.IDocument, int, int)
	 */
	public void setRange(IDocument document, int offset, int length)
	{
		fLastWasWhitespace = false;
		Assert.isLegal(document != null);
		final int documentLength = document.getLength();

		// Check the range
		Assert.isLegal(offset > -1);
		Assert.isLegal(length > -1);
		Assert.isLegal(offset + length <= documentLength);

		this.fOffset = offset;

		try
		{
			fScanner.setSource(document.get(offset, length));
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(JSPlugin.getDefault(), e);
		}
	}

	/**
	 * Gathers the next token based on the JSScanner. Note that it does some manipulations to create whitespace tokens
	 * (which the jflex js scanner does not return).
	 * 
	 * @see org.eclipse.jface.text.rules.ITokenScanner#nextToken()
	 */
	public IToken nextToken()
	{
		try
		{
			Symbol symbol;
			if (fLastWasWhitespace)
			{
				symbol = fLastSymbol;
				fLastWasWhitespace = false;
			}
			else
			{

				symbol = fQueue.poll();
				if (symbol == null)
				{
					symbol = fScanner.nextToken();
				}
				// Emulate whitespace token creation.
				if (symbol.getStart() > fLastSymbol.getEnd() + 1)
				{
					fTokenOffset = fLastSymbol.getEnd() + 1;
					fTokenLen = symbol.getStart() - fLastSymbol.getEnd() - 1;
					fLastSymbol = symbol;
					fLastWasWhitespace = true;
					return Token.WHITESPACE;
				}
				else
				{
					fLastWasWhitespace = false;
				}
			}

			fTokenOffset = symbol.getStart();
			fTokenLen = symbol.getEnd() - symbol.getStart() + 1;
			IToken ret = mapToken(symbol);
			fLastSymbol = symbol;
			return ret;
		}
		catch (Exception e)
		{
			IdeLog.logError(JSPlugin.getDefault(), e);
			return UNDEFINED_TOKEN;
		}
	}

	private IToken mapToken(Symbol token) throws IOException, beaver.Scanner.Exception
	{
		switch (token.getId())
		{

			case Terminals.NULL:
				return NULL_TOKEN;
			case Terminals.TRUE:
				return TRUE_TOKEN;
			case Terminals.FALSE:
				return FALSE_TOKEN;

			case Terminals.DOT:
				return PERIOD_TOKEN;

			case Terminals.VAR:
			case Terminals.VOID:
				return STORAGE_TOKEN;

			case Terminals.EQUAL:
			case Terminals.EQUAL_EQUAL:
			case Terminals.EQUAL_EQUAL_EQUAL:
			case Terminals.GREATER:
			case Terminals.GREATER_EQUAL:
			case Terminals.GREATER_GREATER:
			case Terminals.GREATER_GREATER_EQUAL:
			case Terminals.GREATER_GREATER_GREATER:
			case Terminals.GREATER_GREATER_GREATER_EQUAL:
			case Terminals.REGEX:
			case Terminals.STAR:
			case Terminals.STAR_EQUAL:
			case Terminals.FORWARD_SLASH:
			case Terminals.FORWARD_SLASH_EQUAL:
			case Terminals.PERCENT:
			case Terminals.PERCENT_EQUAL:
			case Terminals.PLUS:
			case Terminals.PLUS_PLUS:
			case Terminals.PLUS_EQUAL:
			case Terminals.MINUS:
			case Terminals.MINUS_MINUS:
			case Terminals.MINUS_EQUAL:
			case Terminals.AMPERSAND:
			case Terminals.AMPERSAND_AMPERSAND:
			case Terminals.AMPERSAND_EQUAL:
			case Terminals.CARET:
			case Terminals.CARET_EQUAL:
			case Terminals.PIPE:
			case Terminals.PIPE_PIPE:
			case Terminals.PIPE_EQUAL:
			case Terminals.LESS:
			case Terminals.LESS_EQUAL:
			case Terminals.LESS_LESS:
			case Terminals.LESS_LESS_EQUAL:
			case Terminals.QUESTION:
			case Terminals.TILDE:
			case Terminals.EXCLAMATION:
			case Terminals.EXCLAMATION_EQUAL:
			case Terminals.EXCLAMATION_EQUAL_EQUAL:
				return OPERATOR_TOKEN;

			case Terminals.DELETE:
			case Terminals.INSTANCEOF:
			case Terminals.IN:
			case Terminals.NEW:
			case Terminals.TYPEOF:
			case Terminals.WITH:
				return KEYWORD_OPERATOR_TOKEN;

			case Terminals.FUNCTION:
				fCurrentState = State.FUNCTION_DECLARATION;
				return FUNCTION_TOKEN;

			case Terminals.LPAREN:
				if (fCurrentState == State.FUNCTION_DECLARATION)
				{
					fCurrentState = State.FUNCTION_DECLARATION_INSIDE_PARENS;
					return FUNCTION_LPAREN_TOKEN;
				}
			case Terminals.RPAREN:
				if (fCurrentState == State.FUNCTION_DECLARATION
						|| fCurrentState == State.FUNCTION_DECLARATION_INSIDE_PARENS)
				{
					fCurrentState = State.DEFAULT;
					return FUNCTION_RPAREN_TOKEN;
				}
				return PARENTHESIS_TOKEN;

			case Terminals.LBRACKET:
			case Terminals.RBRACKET:
				return BRACKET_TOKEN;

			case Terminals.LCURLY:
			case Terminals.RCURLY:
				return CURLY_BRACE_TOKEN;

			case Terminals.SEMICOLON:
				return SEMICOLON_TOKEN;

			case Terminals.THIS:
				return VARIABLE_TOKEN;

			case Terminals.COMMA:
				return COMMA_TOKEN;

			case Terminals.COLON:
				return COLON_TOKEN;

			case Terminals.IF:
			case Terminals.BREAK:
			case Terminals.CASE:
			case Terminals.CATCH:
			case Terminals.CONTINUE:
			case Terminals.DEFAULT:
			case Terminals.DO:
			case Terminals.ELSE:
			case Terminals.FINALLY:
			case Terminals.FOR:
			case Terminals.RETURN:
			case Terminals.SWITCH:
			case Terminals.THROW:
			case Terminals.TRY:
			case Terminals.WHILE:
				return CONTROL_KEYWORD_TOKEN;

			case Terminals.IDENTIFIER:
			{
				{// Function-related handling

					// If we have a name after a function declaration, treat it as a function name.
					if (fCurrentState == State.FUNCTION_DECLARATION)
					{
						return FUNCTION_NAME_TOKEN;
					}
					// If a name is found inside a function declaration, treat is as a parameter.
					if (fCurrentState == State.FUNCTION_DECLARATION_INSIDE_PARENS)
					{
						return FUNCTION_PARAMETER_TOKEN;
					}

					// Handle: a = function(){};
					// We have to do a look-ahead for this use-case.

					// This is a queue with a copy of the tokens we've already looked ahead at this time.
					Queue<Symbol> tempQueue = fQueue.peek() != null ? new LinkedList<Symbol>(fQueue) : null;

					while (true)
					{
						Symbol nextToken = lookAhead(tempQueue);

						if (nextToken.getId() == Terminals.EQUAL)
						{
							nextToken = lookAhead(tempQueue);
							if (nextToken.getId() == Terminals.FUNCTION)
							{
								return FUNCTION_NAME_TOKEN;
							}
							else if (nextToken.getId() == Terminals.IDENTIFIER)
							{
								// Handle a = b = function(){};
								continue;
							}
							else
							{
								break;
							}
						}
						else
						{
							break;
						}
					}
				}

				{ // Give proper types for identifiers that are special somehow.

					// Handle constants
					if (CONSTANTS.contains(token.value))
					{
						return CONSTANT_TOKEN;
					}
					// Handle variables
					if (VARIABLES.contains(token.value))
					{
						return VARIABLE_TOKEN;
					}
					// Handle other keywords
					if (OTHER_KEYWORDS.contains(token.value))
					{
						return OTHER_KEYWORD_TOKEN;
					}
					if (fLastSymbol.getId() == Terminals.DOT)
					{
						// Handle firebug (only after dot).
						if (FIREBUG_FUNCTONS.contains(token.value))
						{
							return FIREBUG_FUNCTION_TOKEN;
						}
					}
					// Handle support classes
					if (SUPPORT_CLASSES.contains(token.value))
					{
						return SUPPORT_CLASS_TOKEN;
					}

					// Handle keyword control future
					if (KEYWORD_CONTROL_FUTURE.contains(token.value))
					{
						return CONTROL_KEYWORD_TOKEN;
					}
					// Handle storage modifiers
					if (STORAGE_MODIFEERS.contains(token.value))
					{
						return STORAGE_MODIFIER_TOKEN;
					}
					// Handle storage types
					if (STORAGE_TYPES.contains(token.value))
					{
						return STORAGE_TOKEN;
					}
					// Handle dom constants
					if (SUPPORT_DOM_CONSTANT.contains(token.value))
					{
						return SUPPORT_DOM_CONSTANT_TOKEN;
					}

				}
				return IDENTIFIER_TOKEN;
			}
			case Terminals.NUMBER:
				return NUMBER_TOKEN;

			case Terminals.EOF:
				return Token.EOF;
			default:
				String msg = "JSCodeScanner: Token not mapped: " + token.getId() + ">>" + token.value + "<<"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				IdeLog.logWarning(JSPlugin.getDefault(), msg);
		}
		return UNDEFINED_TOKEN;
	}

	private Symbol lookAhead(Queue<Symbol> tempQueue) throws IOException, beaver.Scanner.Exception
	{
		Symbol nextToken;
		if (tempQueue == null)
		{
			nextToken = fScanner.nextToken();
			fQueue.add(nextToken);
			return nextToken;
		}

		nextToken = tempQueue.poll();
		if (nextToken != null)
		{
			return nextToken;
		}
		nextToken = fScanner.nextToken();
		fQueue.add(nextToken);
		return nextToken;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#getTokenOffset()
	 */
	public int getTokenOffset()
	{
		return fOffset + fTokenOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#getTokenLength()
	 */
	public int getTokenLength()
	{
		return fTokenLen;
	}

}
