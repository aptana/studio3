package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSThisNode extends JSPrimitiveNode
{
	/**
	 * JSThisNode
	 */
	public JSThisNode()
	{
		super(JSNodeTypes.THIS, "this");
	}

	/**
	 * JSThisNode
	 * 
	 * @param identifier
	 */
	public JSThisNode(Symbol identifier)
	{
		this();
	}
}
