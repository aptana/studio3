package com.aptana.editor.js.parsing.ast;

public class JSStatementsNode extends JSNode
{
	/**
	 * JSStatementsNode
	 * 
	 * @param children
	 */
	public JSStatementsNode(JSNode... children)
	{
		super(JSNodeTypes.STATEMENTS, children);
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
