package com.aptana.editor.js.parsing.ast;

public class JSObjectNode extends JSNaryNode
{
	/**
	 * JSObjectNode
	 * 
	 * @param children
	 */
	public JSObjectNode(JSNode... children)
	{
		super(JSNodeTypes.OBJECT_LITERAL, children);
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
