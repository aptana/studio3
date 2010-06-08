package com.aptana.editor.js.parsing.ast;

public class JSForNode extends JSNode
{
	/**
	 * JSForNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSForNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.FOR, start, end, children);
	}
}
