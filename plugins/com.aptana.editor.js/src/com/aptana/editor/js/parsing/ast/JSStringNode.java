package com.aptana.editor.js.parsing.ast;

public class JSStringNode extends JSPrimitiveNode
{
	/**
	 * JSStringNode
	 * 
	 * @param start
	 * @param end
	 * @param text
	 */
	public JSStringNode(int start, int end, String text)
	{
		super(JSNodeTypes.STRING, start, end, text);
	}
}
