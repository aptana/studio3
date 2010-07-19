package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSConstructNode extends JSNode
{
	/**
	 * JSConstructNode
	 * 
	 * @param children
	 */
	public JSConstructNode(JSNode... children)
	{
		super(JSNodeTypes.CONSTRUCT, children);
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
	 * getArguments
	 * 
	 * @return
	 */
	public IParseNode getArguments()
	{
		return this.getChild(1);
	}

	/**
	 * getIdentifier
	 * 
	 * @return
	 */
	public IParseNode getExpression()
	{
		return this.getChild(0);
	}
}
