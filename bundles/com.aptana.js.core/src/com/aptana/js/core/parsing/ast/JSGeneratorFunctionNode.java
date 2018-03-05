package com.aptana.js.core.parsing.ast;

public class JSGeneratorFunctionNode extends JSFunctionNode
{

	/**
	 * Used by ANTLR AST
	 */
	public JSGeneratorFunctionNode(int start, int end)
	{
		super(start, end);
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
