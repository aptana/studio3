/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.ast;

import beaver.Symbol;

/**
 * @author ayeung
 *
 */
public class ParseError implements IParseError
{
	private Symbol fSymbol;
	private String fMessage;
	private final Severity fSeverity;
	private int fOffset;

	public ParseError(Symbol symbol, Severity severity)
	{
		fSymbol = symbol;
		fMessage = buildErrorMessage(symbol);
		fSeverity = severity;
		if (fSymbol != null)
		{
			fOffset = fSymbol.getStart();
		}
	}

	public ParseError(int offset, String message, Severity severity)
	{
		fSeverity = severity;
		fMessage = message;
		fOffset = offset;
	}

	/* (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseError#getOffset()
	 */
	public int getOffset()
	{
		return fOffset;
	}

	/* (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseError#getMessage()
	 */
	public String getMessage()
	{
		return fMessage;
	}

	public Severity getSeverity()
	{
		return fSeverity;
	}

	private String buildErrorMessage(Symbol token)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(Messages.ParseError_syntax_error_unexpected_token);
		builder.append('"');
		builder.append(token.value);
		builder.append('"');
		return builder.toString();
	}

}
