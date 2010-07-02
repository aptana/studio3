package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSIdentifierNode extends JSPrimitiveNode
{
	/**
	 * JSIdentifierNode
	 * 
	 * @param text
	 */
	public JSIdentifierNode(String text)
	{
		super(JSNodeTypes.IDENTIFIER, text);
	}

	/**
	 * JSIdentifierNode
	 * 
	 * @param identifier
	 */
	public JSIdentifierNode(Symbol identifier)
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
