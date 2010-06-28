package com.aptana.parsing.ast;

import java.util.ArrayList;
import java.util.List;

import beaver.Symbol;

public class ParseRootNode extends ParseNode
{

	public ParseRootNode(String language, Symbol[] children, int start, int end)
	{
		super(language);
		this.start = start;
		this.end = end;

		List<IParseNode> nodes = new ArrayList<IParseNode>();
		for (Symbol child : children)
		{
			if (child instanceof IParseNode)
			{
				nodes.add((IParseNode) child);
			}
		}
		setChildren(nodes.toArray(new IParseNode[nodes.size()]));
	}
}
