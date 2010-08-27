package com.aptana.editor.js.parsing.ast;

public class JSThrowNode extends JSPreUnaryOperatorNode
{
	/**
	 * JSThrowNode
	 * 
	 * @param expression
	 */
	public JSThrowNode(JSNode expression)
	{
		super(JSNodeTypes.THROW, expression);
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
