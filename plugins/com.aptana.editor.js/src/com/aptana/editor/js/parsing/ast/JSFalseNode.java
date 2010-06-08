package com.aptana.editor.js.parsing.ast;

public class JSFalseNode extends JSPrimitiveNode
{
	/**
	 * JSFalseNode
	 * 
	 * @param text
	 * @param start
	 * @param end
	 */
	public JSFalseNode(String text, int start, int end)
	{
		super(JSNodeTypes.FALSE, text, start, end);
	}
}
