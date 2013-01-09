/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import beaver.Symbol;

import com.aptana.css.core.parsing.CSSTokenTypeSymbol;
import com.aptana.editor.common.parsing.AbstractFlexPartitionScanner;
import com.aptana.editor.css.parsing.CSSPartitionFlexScanner;

/**
 * A partition scanner for CSS code.
 */
public class CSSSourcePartitionScannerJFlex extends AbstractFlexPartitionScanner
{

	private static final Token STRING_DOUBLE_TOKEN = new Token(CSSSourceConfiguration.STRING_DOUBLE);
	private static final Token STRING_SINGLE_TOKEN = new Token(CSSSourceConfiguration.STRING_SINGLE);
	private static final Token MULTILINE_COMMENT_TOKEN = new Token(CSSSourceConfiguration.MULTILINE_COMMENT);

	public CSSSourcePartitionScannerJFlex()
	{
		super(new CSSPartitionFlexScanner());
	}

	@Override
	protected void setSource(String source)
	{
		((CSSPartitionFlexScanner) fScanner).setSource(source);

	}

	@Override
	protected IToken mapToken(Symbol symbol, Symbol symbolStart, boolean returnDefaultContentType)
	{
		switch (((CSSTokenTypeSymbol) symbol).token)
		{
			case DOUBLE_QUOTED_STRING:
				return returnToken(symbol, symbolStart, returnDefaultContentType, symbol.getEnd() - symbol.getStart()
						+ 1, STRING_DOUBLE_TOKEN);

			case SINGLE_QUOTED_STRING:
				return returnToken(symbol, symbolStart, returnDefaultContentType, symbol.getEnd() - symbol.getStart()
						+ 1, STRING_SINGLE_TOKEN);

			case COMMENT:
				return returnToken(symbol, symbolStart, returnDefaultContentType, symbol.getEnd() - symbol.getStart()
						+ 1, MULTILINE_COMMENT_TOKEN);

			case EOF:
				return returnToken(symbol, symbolStart, returnDefaultContentType, 0, Token.EOF);
		}
		return null;
	}

}
