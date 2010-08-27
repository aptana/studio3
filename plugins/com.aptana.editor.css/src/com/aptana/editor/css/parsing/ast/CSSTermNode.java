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
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof CSSTermNode))
		{
			return false;
		}
		CSSTermNode other = (CSSTermNode) obj;
		return fTerm.equals(other.fTerm);
	}

	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + fTerm.hashCode();
	}

	@Override
	public String toString()
	{
		return fTerm;
	}
}
