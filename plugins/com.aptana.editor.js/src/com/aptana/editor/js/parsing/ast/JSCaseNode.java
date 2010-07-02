package com.aptana.editor.js.parsing.ast;

public class JSCaseNode extends JSNaryAndExpressionNode
{
	/**
	 * JSCaseNode
	 * 
	 * @param children
	 */
	public JSCaseNode(JSNode... children)
	{
		super(JSNodeTypes.CASE, children);
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
