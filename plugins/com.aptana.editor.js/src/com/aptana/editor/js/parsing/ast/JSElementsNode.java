package com.aptana.editor.js.parsing.ast;

public class JSElementsNode extends JSNaryNode
{
	/**
	 * JSElementsNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSElementsNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.ELEMENTS, start, end, children);
	}
}
