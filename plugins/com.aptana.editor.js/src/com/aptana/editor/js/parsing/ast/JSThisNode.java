package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSThisNode extends JSPrimitiveNode
{
	/**
	 * JSThisNode
	 * 
	 * @param identifier
	 */
	public JSThisNode(Symbol identifier)
	{
		this(identifier.getStart(), identifier.getEnd());
	}

	/**
	 * JSThisNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSThisNode(int start, int end)
	{
		super(JSNodeTypes.THIS, start, end, "this");
	}
}
