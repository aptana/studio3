package com.aptana.editor.js.parsing.ast;

public class JSCommaNode extends JSNode
{
	/**
	 * JSCommaNode
	 * 
	 * @param children
	 */
	public JSCommaNode(JSNode... children)
	{
		super(JSNodeTypes.COMMA, children);
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
