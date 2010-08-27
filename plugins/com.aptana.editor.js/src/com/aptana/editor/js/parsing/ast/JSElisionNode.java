package com.aptana.editor.js.parsing.ast;

public class JSElisionNode extends JSNode
{
	/**
	 * JSElisionNode
	 * 
	 * @param children
	 */
	public JSElisionNode(JSNode... children)
	{
		super(JSNodeTypes.ELISION, children);
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
