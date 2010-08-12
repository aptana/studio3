package com.aptana.editor.js.parsing.ast;

public class JSParametersNode extends JSNode
{
	/**
	 * JSParametersNode
	 * 
	 * @param children
	 */
	public JSParametersNode(JSNode... children)
	{
		super(JSNodeTypes.PARAMETERS, children);
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
