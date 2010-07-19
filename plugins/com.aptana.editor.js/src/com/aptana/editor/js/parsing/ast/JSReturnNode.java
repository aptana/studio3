package com.aptana.editor.js.parsing.ast;

public class JSReturnNode extends JSPreUnaryOperatorNode
{
	/**
	 * JSReturnNode
	 * 
	 * @param expression
	 */
	public JSReturnNode(JSNode expression)
	{
		super(JSNodeTypes.RETURN, expression);
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
