package com.aptana.editor.js.parsing.ast;

public class JSIfNode extends JSNode
{
	/**
	 * JSIfNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSIfNode(int start, int end, JSNode ... children)
	{
		super(JSNodeTypes.IF, start, end, children);
	}
}
