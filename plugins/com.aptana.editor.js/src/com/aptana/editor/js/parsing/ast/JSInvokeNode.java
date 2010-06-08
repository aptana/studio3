package com.aptana.editor.js.parsing.ast;

public class JSInvokeNode extends JSNode
{
	/**
	 * JSInvokeNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSInvokeNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.INVOKE, start, end, children);
	}
}
