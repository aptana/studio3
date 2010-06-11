package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSNullNode extends JSPrimitiveNode
{
	/**
	 * JSNullNode
	 * 
	 * @param identifier
	 */
	public JSNullNode(Symbol identifier)
	{
		this(identifier.getStart(), identifier.getEnd());
	}
	
	/**
	 * JSNullNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSNullNode(int start, int end)
	{
		super(JSNodeTypes.NULL, start, end, "null");
	}
}
