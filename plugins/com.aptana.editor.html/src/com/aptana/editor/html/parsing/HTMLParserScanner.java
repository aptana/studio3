/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import org.eclipse.jface.text.rules.IToken;

import beaver.Symbol;

import com.aptana.editor.common.parsing.CompositeParserScanner;
import com.aptana.editor.html.parsing.lexer.HTMLTokens;

public class HTMLParserScanner extends CompositeParserScanner
{

	public HTMLParserScanner()
	{
		this(new HTMLScanner());
	}

	protected HTMLParserScanner(HTMLScanner tokenScanner)
	{
		super(tokenScanner);
	}

	@Override
	protected Symbol createSymbol(int start, int end, String text, IToken token)
	{
		short type = HTMLTokens.EOF;
		Object data = token.getData();
		if (data != null)
		{
			type = ((HTMLScanner) getTokenScanner()).getTokenType(data);
		}
		return new Symbol(type, start, end, text);
	}

	@Override
	protected boolean isIgnored(IToken token)
	{
		if (super.isIgnored(token))
		{
			return true;
		}
		Object data = token.getData();
		if (data == null)
		{
			return false;
		}
		// ignores doctype declaration
		return data.equals(HTMLTokens.getTokenName(HTMLTokens.DOCTYPE));
	}
}
