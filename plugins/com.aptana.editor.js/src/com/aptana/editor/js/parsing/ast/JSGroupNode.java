package com.aptana.editor.js.parsing.ast;

public class JSGroupNode extends JSUnaryOperatorNode
{
	/**
	 * JSGroupNode
	 * 
	 * @param start
	 * @param end
	 * @param expression
	 */
	public JSGroupNode(int start, int end, JSNode expression)
	{
		super(JSNodeTypes.GROUP, start, end, expression);
	}
}
