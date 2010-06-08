package com.aptana.editor.js.parsing.ast;

public class JSThrowNode extends JSUnaryOperatorNode
{
	/**
	 * JSThrowNode
	 * 
	 * @param start
	 * @param end
	 * @param expression
	 */
	public JSThrowNode(int start, int end, JSNode expression)
	{
		super(JSNodeTypes.THROW, start, end, expression);
	}
}
