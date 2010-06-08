package com.aptana.editor.js.parsing.ast;

public class JSCaseNode extends JSNaryAndExpressionNode
{
	/**
	 * JSCaseNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSCaseNode(int start, int end, JSNode ... children)
	{
		super(JSNodeTypes.CASE, start, end, children);
	}
}
