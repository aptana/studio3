package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public abstract class JSNaryAndExpressionNode extends JSNaryNode
{
	/**
	 * JSNaryAndExpressionNode
	 * 
	 * @param type
	 * @param children
	 */
	public JSNaryAndExpressionNode(short type, JSNode... children)
	{
		super(type, children);
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
