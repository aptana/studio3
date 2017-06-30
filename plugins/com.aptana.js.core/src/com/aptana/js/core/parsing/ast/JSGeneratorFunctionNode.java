package com.aptana.js.core.parsing.ast;

public class JSGeneratorFunctionNode extends JSFunctionNode
{
	public JSGeneratorFunctionNode(JSNode name, JSParametersNode params, JSStatementsNode body)
	{
		super(name, params, body);
	}

	/**
	 * Used by ANTLR AST
	 */
	public JSGeneratorFunctionNode()
	{
		super();
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
