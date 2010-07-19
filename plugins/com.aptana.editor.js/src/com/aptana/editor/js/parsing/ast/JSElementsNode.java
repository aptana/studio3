package com.aptana.editor.js.parsing.ast;

public class JSElementsNode extends JSNode
{
	/**
	 * JSElementsNode
	 * 
	 * @param children
	 */
	public JSElementsNode(JSNode... children)
	{
		super(JSNodeTypes.ELEMENTS, children);
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
