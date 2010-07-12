package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSThisNode extends JSPrimitiveNode
{
	/**
	 * JSThisNode
	 */
	public JSThisNode()
	{
		super(JSNodeTypes.THIS, "this"); //$NON-NLS-1$
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
