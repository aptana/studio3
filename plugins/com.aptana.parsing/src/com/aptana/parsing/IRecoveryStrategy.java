/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

import java.io.IOException;

import beaver.Parser;
import beaver.Parser.TokenStream;
import beaver.Symbol;

public interface IRecoveryStrategy
{
	/**
	 * This method is used to attempt to recover from a parse error encountered in a Beaver parser.
	 * 
	 * @param parser
	 *            The parser where the error occurred
	 * @param lastToken
	 *            The last token that has been successfully consumed by the parser
	 * @param currentToken
	 *            The current token where the error occurred
	 * @param in
	 *            The token stream used by the parser
	 * @param events
	 *            The error logger associated with parser
	 * @return
	 */
	boolean recover(IParser parser, Symbol lastToken, Symbol currentToken, TokenStream in, Parser.Events events)
			throws IOException;
}
