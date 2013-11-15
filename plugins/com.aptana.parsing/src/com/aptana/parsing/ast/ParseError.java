/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.ast;

import beaver.Symbol;

import com.aptana.core.build.Problem;

/**
 * @author cwilliams
 * @author ayeung
 */
public class ParseError extends Problem implements IParseError
{
	private String fLanguage;

	public ParseError(String language, Symbol symbol, Severity severity)
	{
		this(language, symbol, null, severity);
	}

	public ParseError(String language, Symbol symbol, String message, Severity severity)
	{
		this(language, symbol != null ? symbol.getStart() : 0, symbol != null ? symbol.getEnd() - symbol.getStart() + 1
				: 0, message == null ? buildErrorMessage(symbol) : message, severity);
	}

	public ParseError(String language, int offset, int length, String message, Severity severity)
	{
		super(severity == null ? Severity.WARNING.intValue() : severity.intValue(), message, offset, length, -1, null);
		fLanguage = language;
	}

	private static String buildErrorMessage(Symbol token)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(Messages.ParseError_syntax_error_unexpected_token);
		builder.append('"');
		builder.append(token.value);
		builder.append('"');
		return builder.toString();
	}

	public String getLangauge()
	{
		return fLanguage;
	}

}
