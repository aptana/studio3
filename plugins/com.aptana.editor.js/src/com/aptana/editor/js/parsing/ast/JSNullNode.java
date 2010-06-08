package com.aptana.editor.js.parsing.ast;

public class JSNullNode extends JSPrimitiveNode
{
	/**
	 * JSNullNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSNullNode(int start, int end)
	{
		super(JSNodeTypes.NULL, start, end, "null");
	}
}
