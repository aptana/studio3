package com.aptana.editor.js.parsing;

import beaver.Symbol;

import com.aptana.editor.js.parsing.lexer.JSTokenType;

public class JSTokenTypeSymbol extends Symbol
{

	public final JSTokenType token;

	public JSTokenTypeSymbol(JSTokenType id, int left, int right, Object value)
	{
		super(id.getIndex(), left, right, value);
		this.token = id;
	}

}
