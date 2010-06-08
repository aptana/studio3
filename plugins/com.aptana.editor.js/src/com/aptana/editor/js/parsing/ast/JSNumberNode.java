package com.aptana.editor.js.parsing.ast;

public class JSNumberNode extends JSPrimitiveNode
{
	/**
	 * JSNumberNode
	 * 
	 * @param text
	 * @param start
	 * @param end
	 */
	public JSNumberNode(String text, int start, int end)
	{
		super(JSNodeTypes.NUMBER, text, start, end);
	}
}
