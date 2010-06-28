package com.aptana.editor.js.parsing.ast;

public class JSElisionNode extends JSNaryNode
{
	/**
	 * JSElisionNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSElisionNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.ELISION, start, end, children);
	}
}
