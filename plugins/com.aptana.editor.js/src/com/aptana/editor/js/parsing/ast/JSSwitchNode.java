package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSSwitchNode extends JSNode
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

	/**
	 * getExpression
	 * 
	 * @return
	 */
	public IParseNode getExpression()
	{
		return this.getChild(0);
	}
}
