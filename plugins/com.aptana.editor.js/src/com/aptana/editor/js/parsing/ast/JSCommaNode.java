package com.aptana.editor.js.parsing.ast;

public class JSCommaNode extends JSNaryNode
{
	/**
	 * JSCommaNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSCommaNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.COMMA, start, end, children);
	}
}
