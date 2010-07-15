package com.aptana.editor.js.parsing.ast;

public class JSNaryAndExpressionNode extends JSNaryNode
{
	/**
	 * JSNaryAndExpressionNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 * @param expression
	 * @param statements
	 */
	public JSNaryAndExpressionNode(short type, int start, int end, JSNode... children)
	{
		super(type, start, end, children);
	}
}
