package com.aptana.editor.js.parsing.ast;

public class JSReturnNode extends JSUnaryOperatorNode
{
	/**
	 * JSReturnNode
	 * 
	 * @param start
	 * @param end
	 * @param expression
	 */
	public JSReturnNode(int start, int end, JSNode expression)
	{
		super(JSNodeTypes.RETURN, start, end, expression);
	}
}
