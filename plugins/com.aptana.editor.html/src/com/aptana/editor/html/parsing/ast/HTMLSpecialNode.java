package com.aptana.editor.html.parsing.ast;

import beaver.Symbol;

import com.aptana.parsing.ast.IParseNode;

public class HTMLSpecialNode extends HTMLElementNode
{

	public HTMLSpecialNode(Symbol tag, IParseNode[] children, int start, int end)
	{
		super(tag, start, end);
		setChildren(children);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof HTMLSpecialNode))
		{
			return false;
		}
		return super.equals(obj);
	}
}
