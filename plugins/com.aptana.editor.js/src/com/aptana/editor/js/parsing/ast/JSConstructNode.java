package com.aptana.editor.js.parsing.ast;

public class JSConstructNode extends JSNode
{
	/**
	 * JSConstructNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSConstructNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.CONSTRUCT, start, end, children);
	}
}
