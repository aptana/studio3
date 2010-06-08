package com.aptana.editor.js.parsing.ast;

public class JSNumberNode extends JSPrimitiveNode
{
	/**
	 * JSNumberNode
	 * 
	 * @param start
	 * @param end
	 * @param text
	 */
	public JSNumberNode(int start, int end, String text)
	{
		super(JSNodeTypes.NUMBER, start, end, text);
	}
}
