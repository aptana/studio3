package com.aptana.js.core.parsing.ast;

public class JSArrowFunctionNode extends JSNode
{

	public JSArrowFunctionNode(JSParametersNode params, JSNode body)
	{
		super(IJSNodeTypes.ARROW_FUNCTION, params, body);
	}

	/**
	 * Used by ANTLR AST
	 */
	public JSArrowFunctionNode()
	{
		super(IJSNodeTypes.ARROW_FUNCTION);
	}

	public JSParametersNode getParameters()
	{
		return (JSParametersNode) getChild(0);
	}

	public JSNode getBody()
	{
		return (JSNode) getChild(1);
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
