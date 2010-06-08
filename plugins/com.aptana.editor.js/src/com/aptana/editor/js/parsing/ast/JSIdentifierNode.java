package com.aptana.editor.js.parsing.ast;

public class JSIdentifierNode extends JSPrimitiveNode
{
	/**
	 * JSIdentifierNode
	 * 
	 * @param text
	 * @param start
	 * @param end
	 */
	public JSIdentifierNode(String text, int start, int end)
	{
		super(JSNodeTypes.IDENTIFIER, text, start, end);
	}
}
