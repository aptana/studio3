package com.aptana.editor.js.parsing.ast;

public class JSWithNode extends JSNode
{
	/**
	 * JSWithNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSWithNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.WITH, start, end, children);
	}
}
