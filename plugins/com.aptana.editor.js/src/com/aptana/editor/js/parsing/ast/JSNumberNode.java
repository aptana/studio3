package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSNumberNode extends JSPrimitiveNode
{
	/**
	 * JSNumberNode
	 * 
	 * @param text
	 */
	public JSNumberNode(String text)
	{
		super(JSNodeTypes.NUMBER, text);
	}

	/**
	 * JSNumberNode
	 * 
	 * @param identifier
	 */
	public JSNumberNode(Symbol identifier)
	{
		this((String) identifier.value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#accept(com.aptana.editor.js.parsing.ast.JSTreeWalker)
	 */
	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}
}
