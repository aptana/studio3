/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import beaver.Scanner.Exception;
import beaver.Symbol;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.parsing.AbstractFlexTokenScanner;
import com.aptana.editor.json.text.rules.IJSONScopes;
import com.aptana.json.core.parsing.JSONFlexScanner;
import com.aptana.json.core.parsing.Terminals;

public class JSONSourceScanner extends AbstractFlexTokenScanner
{

	private static final Token UNDEFINED_TOKEN = new Token(IJSONScopes.UNDEFINED);
	private static final Token NUMBER_TOKEN = new Token(IJSONScopes.NUMBER);
	private static final Token KEYWORD_OPERATOR = new Token(IJSONScopes.KEYWORD_OPERATOR);
	private static final Token NULL = new Token(IJSONScopes.NULL);
	private static final Token TRUE = new Token(IJSONScopes.TRUE);
	private static final Token FALSE = new Token(IJSONScopes.FALSE);
	private static final Token COMMA = new Token(IJSONScopes.COMMA);
	private static final Token CURLY = new Token(IJSONScopes.CURLY);
	private static final Token BRACKET = new Token(IJSONScopes.BRACKET);

	protected JSONSourceScanner()
	{
		super(new JSONFlexScanner());
	}

	@Override
	protected void setSource(String source)
	{
		((JSONFlexScanner) fScanner).setSource(source);
	}

	@Override
	protected IToken getUndefinedToken()
	{
		return UNDEFINED_TOKEN;
	}

	@Override
	protected IToken mapToken(Symbol token) throws IOException, Exception
	{
		switch (token.getId())
		{
			case Terminals.NULL:
				return NULL;
			case Terminals.TRUE:
				return TRUE;
			case Terminals.FALSE:
				return FALSE;

			case Terminals.COMMA:
				return COMMA;

			case Terminals.LBRACKET:
			case Terminals.RBRACKET:
				return BRACKET;

			case Terminals.LCURLY:
			case Terminals.RCURLY:
				return CURLY;

			case Terminals.COLON:
				return KEYWORD_OPERATOR;

			case Terminals.NUMBER:
				return NUMBER_TOKEN;

			case Terminals.EOF:
				return Token.EOF;
			default:
				IdeLog.logWarning(JSONPlugin.getDefault(), MessageFormat.format(
						"JSONSourceScanner: Token not mapped: {0}>>{1}<<", token.getId(), token.value)); //$NON-NLS-1$
		}
		return UNDEFINED_TOKEN;
	}
}
