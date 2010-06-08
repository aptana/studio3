package com.aptana.editor.js.parsing.ast;

public class JSWhileNode extends JSNode
{
	/**
	 * JSWhileNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSWhileNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.WHILE, start, end, children);
	}
}
