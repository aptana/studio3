package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSConditionalNode extends JSNode
{
	/**
	 * JSConditionalNode
	 * 
	 * @param children
	 */
	public JSConditionalNode(JSNode... children)
	{
		super(JSNodeTypes.CONDITIONAL, children);
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
	 * getFalseExpression
	 * 
	 * @return
	 */
	public IParseNode getFalseExpression()
	{
		return this.getChild(2);
	}

	/**
	 * getTestExpression
	 * 
	 * @return
	 */
	public IParseNode getTestExpression()
	{
		return this.getChild(0);
	}

	/**
	 * getTrueExpression
	 * 
	 * @return
	 */
	public IParseNode getTrueExpression()
	{
		return this.getChild(1);
	}
}
