package com.aptana.editor.js.parsing.ast;

public class JSSwitchNode extends JSNaryAndExpressionNode
{
	/**
	 * JSSwitchNode
	 * 
	 * @param children
	 */
	public JSSwitchNode(JSNode... children)
	{
		super(JSNodeTypes.SWITCH, children);
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
