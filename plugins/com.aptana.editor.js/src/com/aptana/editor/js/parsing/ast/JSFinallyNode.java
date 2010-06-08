package com.aptana.editor.js.parsing.ast;

public class JSFinallyNode extends JSNode
{
	/**
	 * JSFinallyNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSFinallyNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.FINALLY, start, end, children);
	}
}
