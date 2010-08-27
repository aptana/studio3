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
		// ignores comments and doctype declaration
		return data.equals(HTMLTokens.getTokenName(HTMLTokens.COMMENT))
				|| data.equals(HTMLTokens.getTokenName(HTMLTokens.DOCTYPE));
	}
}
