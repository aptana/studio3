package com.aptana.editor.js.parsing.ast;

public class JSConditionalNode extends JSNode
{
	/**
	 * JSConditionalNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSConditionalNode(int start, int end, JSNode ... children)
	{
		super(JSNodeTypes.CONDITIONAL, start, end, children);
	}
}
