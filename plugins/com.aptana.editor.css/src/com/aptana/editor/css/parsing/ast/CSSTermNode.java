package com.aptana.editor.css.parsing.ast;

import beaver.Symbol;

public class CSSTermNode extends CSSExpressionNode
{

	private String fTerm;

	public CSSTermNode(Symbol term)
	{
		super(term.getStart(), term.getEnd());
		fTerm = term.value.toString();
	}

	@Override
	public String toString()
	{
		return fTerm;
	}
}
