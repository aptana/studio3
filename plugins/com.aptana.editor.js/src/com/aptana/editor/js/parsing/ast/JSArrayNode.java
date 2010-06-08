package com.aptana.editor.js.parsing.ast;

public class JSArrayNode extends JSNaryNode
{
	/**
	 * JSArrayNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSArrayNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.ARRAY_LITERAL, start, end, children);
	}
}
