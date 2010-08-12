package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSTrueNode extends JSPrimitiveNode
{
	/**
	 * JSTrueNode
	 */
	public JSTrueNode()
	{
		super(JSNodeTypes.TRUE, "true"); //$NON-NLS-1$
	}

	/**
	 * JSTrueNode
	 * 
	 * @param identifier
	 */
	public JSTrueNode(Symbol identifier)
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
