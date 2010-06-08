package com.aptana.editor.js.parsing.ast;

public class JSForInNode extends JSNode
{
	/**
	 * JSForInNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSForInNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.FOR_IN, start, end, children);
	}
}
