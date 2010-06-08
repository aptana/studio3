package com.aptana.editor.js.parsing.ast;

public class JSDoNode extends JSNode
{
	/**
	 * JSDoNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSDoNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.DO, start, end, children);
	}
}
