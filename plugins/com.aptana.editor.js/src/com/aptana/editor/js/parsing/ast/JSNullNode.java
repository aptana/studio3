package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSNullNode extends JSPrimitiveNode
{
	/**
	 * JSNullNode
	 */
	public JSNullNode()
	{
		super(JSNodeTypes.NULL, "null");
	}

	/**
	 * JSNullNode
	 * 
	 * @param identifier
	 */
	public JSNullNode(Symbol identifier)
	{
		super(JSNodeTypes.NULL, (String) identifier.value);
	}
}
