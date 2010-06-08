package com.aptana.editor.js.parsing.ast;

public class JSNullNode extends JSPrimitiveNode
{
	/**
	 * JSNullNode
	 * 
	 * @param text
	 * @param start
	 * @param end
	 */
	public JSNullNode(String text, int start, int end)
	{
		super(JSNodeTypes.NULL, text, start, end);
	}
}
