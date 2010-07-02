package com.aptana.editor.js.parsing.ast;

public class JSArrayNode extends JSNaryNode
{
	/**
	 * JSArrayNode
	 * 
	 * @param children
	 */
	public JSArrayNode(JSNode... children)
	{
		super(JSNodeTypes.ARRAY_LITERAL, children);
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
