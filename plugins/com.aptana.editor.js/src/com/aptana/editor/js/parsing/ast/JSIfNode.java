package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSIfNode extends JSNode
{
	/**
	 * JSIfNode
	 * 
	 * @param children
	 */
	public JSIfNode(JSNode... children)
	{
		super(JSNodeTypes.IF, children);
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

	/**
	 * getFalseBlock
	 * 
	 * @return
	 */
	public IParseNode getFalseBlock()
	{
		return this.getChild(2);
	}

	/**
	 * getTrueBlock
	 * 
	 * @return
	 */
	public IParseNode getTrueBlock()
	{
		return this.getChild(1);
	}
}
