package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSNullNode extends JSPrimitiveNode
{
	/**
	 * JSNullNode
	 */
	public JSNullNode()
	{
		super(JSNodeTypes.NULL, "null"); //$NON-NLS-1$
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
