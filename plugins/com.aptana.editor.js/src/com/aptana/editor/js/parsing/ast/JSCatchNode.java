package com.aptana.editor.js.parsing.ast;

public class JSCatchNode extends JSNode
{
	/**
	 * JSCatchNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSCatchNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.CATCH, start, end, children);
	}
}
