/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import beaver.Symbol;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.common.parsing.AbstractFlexTokenScanner;
import com.aptana.editor.js.JSPlugin;
import com.aptana.js.core.JSLanguageConstants;
import com.aptana.js.core.parsing.JSFlexScanner;
import com.aptana.js.core.parsing.Terminals;

/**
 * Code scanner for Javascript based on JFlex.
 */
public class JSCodeScanner extends AbstractFlexTokenScanner
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

	private static final Set<String> CONSTANTS = CollectionsUtil.newSet("Infinity", "NaN", "undefined"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final Set<String> VARIABLES = CollectionsUtil.newSet("super", "this"); //$NON-NLS-1$//$NON-NLS-2$
	private static final Set<String> OTHER_KEYWORDS = CollectionsUtil.newSet("debugger"); //$NON-NLS-1$
	private static final Set<String> SUPPORT_CLASSES = CollectionsUtil.newSet(JSLanguageConstants.SUPPORT_CLASSES);
	private static final Set<String> FIREBUG_FUNCTONS = CollectionsUtil.newSet("log"); //$NON-NLS-1$
	private static final Set<String> KEYWORD_CONTROL_FUTURE = CollectionsUtil
			.newSet(JSLanguageConstants.KEYWORD_CONTROL_FUTURE);
	private static final Set<String> STORAGE_TYPES = CollectionsUtil.newSet(JSLanguageConstants.STORAGE_TYPES);
	private static final Set<String> STORAGE_MODIFEERS = CollectionsUtil.newSet(JSLanguageConstants.STORAGE_MODIFIERS);
	private static final Set<String> SUPPORT_DOM_CONSTANT = CollectionsUtil
			.newSet(JSLanguageConstants.SUPPORT_DOM_CONSTANTS);

	private enum State
	{
		DEFAULT, FUNCTION_DECLARATION, FUNCTION_DECLARATION_INSIDE_PARENS,
	}

	/**
	 * Used to know in which state we're in (i.e.: detect function).
	 */
	private State fCurrentState = State.DEFAULT;

	public JSCodeScanner()
	{
		super(new JSFlexScanner());
	}

	protected void setSource(String string)
	{
		// It shouldn't really collect comments anyways as we're just processing the default partition,
		// so, this is just a safety measure.
		((JSFlexScanner) fScanner).setCollectComments(false);
		((JSFlexScanner) fScanner).setSource(string);
	}

	protected IToken getUndefinedToken()
	{
		return UNDEFINED_TOKEN;
	}

	protected IToken mapToken(Symbol token) throws IOException, beaver.Scanner.Exception
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
			case Terminals.GET:
			case Terminals.SET:
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

			case Terminals.DEBUGGER:
			case Terminals.CLASS:
			case Terminals.ENUM:
			case Terminals.EXPORT:
			case Terminals.EXTENDS:
			case Terminals.IMPORT:
			case Terminals.SUPER:
			case Terminals.IMPLEMENTS:
			case Terminals.INTERFACE:
			case Terminals.LET:
			case Terminals.PACKAGE:
			case Terminals.PRIVATE:
			case Terminals.PROTECTED:
			case Terminals.PUBLIC:
			case Terminals.STATIC:
			case Terminals.YIELD:
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
					Queue<Symbol> tempQueue = fLookAheadQueue.peek() != null ? new LinkedList<Symbol>(fLookAheadQueue)
							: null;

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

}
