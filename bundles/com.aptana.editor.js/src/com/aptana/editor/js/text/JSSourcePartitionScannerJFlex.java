/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import beaver.Symbol;

import com.aptana.editor.common.parsing.AbstractFlexPartitionScanner;
import com.aptana.editor.js.JSSourceConfiguration;

/**
 * A partition scanner for Javascript code.
 */
public class JSSourcePartitionScannerJFlex extends AbstractFlexPartitionScanner
{

	private static final Token JS_DOC_TOKEN = new Token(JSSourceConfiguration.JS_DOC);
	private static final Token STRING_DOUBLE_TOKEN = new Token(JSSourceConfiguration.STRING_DOUBLE);
	private static final Token STRING_SINGLE_TOKEN = new Token(JSSourceConfiguration.STRING_SINGLE);
	private static final Token REGEXP_TOKEN = new Token(JSSourceConfiguration.JS_REGEXP);
	private static final Token MULTILINE_COMMENT_TOKEN = new Token(JSSourceConfiguration.JS_MULTILINE_COMMENT);
	private static final Token SINGLELINE_COMMENT_TOKEN = new Token(JSSourceConfiguration.JS_SINGLELINE_COMMENT);
	private static final Token TEMPLATE_TOKEN = new Token(JSSourceConfiguration.JS_TEMPLATE);

	public JSSourcePartitionScannerJFlex()
	{
		super(new JSPartitioningFlexScanner());
	}

	@Override
	protected void setSource(String source)
	{
		((JSPartitioningFlexScanner) fScanner).setSource(source);
	}

	@Override
	protected IToken mapToken(Symbol symbol, Symbol symbolStart, boolean returnDefaultContentType)
	{
		switch (((JSTokenTypeSymbol) symbol).token)
		{
			case SDOC:
				return returnToken(symbol, symbolStart, returnDefaultContentType, symbol.getEnd() - symbol.getStart()
						+ 1, JS_DOC_TOKEN);

			case STRING_DOUBLE:
				return returnToken(symbol, symbolStart, returnDefaultContentType, symbol.getEnd() - symbol.getStart()
						+ 1, STRING_DOUBLE_TOKEN);

			case STRING_SINGLE:
				return returnToken(symbol, symbolStart, returnDefaultContentType, symbol.getEnd() - symbol.getStart()
						+ 1, STRING_SINGLE_TOKEN);

			case REGEX:
				return returnToken(symbol, symbolStart, returnDefaultContentType, symbol.getEnd() - symbol.getStart()
						+ 1, REGEXP_TOKEN);

			case MULTILINE_COMMENT:
				return returnToken(symbol, symbolStart, returnDefaultContentType, symbol.getEnd() - symbol.getStart()
						+ 1, MULTILINE_COMMENT_TOKEN);

			case SINGLELINE_COMMENT:
				return returnToken(symbol, symbolStart, returnDefaultContentType, symbol.getEnd() - symbol.getStart()
						+ 1, SINGLELINE_COMMENT_TOKEN);
				
			case TEMPLATE_HEAD:
			case TEMPLATE_MIDDLE:
			case TEMPLATE_TAIL:
			case NO_SUB_TEMPLATE:
				return returnToken(symbol, symbolStart, returnDefaultContentType, symbol.getEnd() - symbol.getStart()
						+ 1, TEMPLATE_TOKEN);

			case EOF:
				return returnToken(symbol, symbolStart, returnDefaultContentType, 0, Token.EOF);
		}
		return null;
	}

}
