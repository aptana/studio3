package com.aptana.editor.js.parsing.ast;

public class JSObjectNode extends JSNaryNode
{
	/**
	 * JSObjectNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSObjectNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.OBJECT_LITERAL, start, end, children);
	}
}
