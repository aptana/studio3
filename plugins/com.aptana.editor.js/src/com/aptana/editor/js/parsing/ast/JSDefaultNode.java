package com.aptana.editor.js.parsing.ast;

public class JSDefaultNode extends JSNaryNode
{
	/**
	 * JSDefaultNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSDefaultNode(JSNode... children)
	{
		super(JSNodeTypes.DEFAULT, children);
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
