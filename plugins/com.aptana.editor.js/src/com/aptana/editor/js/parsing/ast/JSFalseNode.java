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
	public JSFalseNode(int start, int end)
	{
		super(JSNodeTypes.FALSE, start, end, "false");
	}
}
