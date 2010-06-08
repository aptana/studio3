package com.aptana.editor.js.parsing.ast;

public class JSStringNode extends JSPrimitiveNode
{
	/**
	 * JSStringNode
	 * 
	 * @param text
	 * @param start
	 * @param end
	 */
	public JSStringNode(String text, int start, int end)
	{
		super(JSNodeTypes.STRING, text, start, end);
	}
}
