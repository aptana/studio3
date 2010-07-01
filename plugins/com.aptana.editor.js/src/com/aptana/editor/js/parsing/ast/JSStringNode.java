package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSStringNode extends JSPrimitiveNode
{
	/**
	 * JSStringNode
	 * 
	 * @param identifier
	 */
	public JSStringNode(Symbol identifier)
	{
		this((String) identifier.value);
	}

	/**
	 * JSStringNode
	 * 
	 * @param text
	 */
	public JSStringNode(String text)
	{
		super(JSNodeTypes.STRING, text);
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
