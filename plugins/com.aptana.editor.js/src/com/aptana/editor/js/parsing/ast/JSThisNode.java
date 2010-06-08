package com.aptana.editor.js.parsing.ast;

public class JSThisNode extends JSPrimitiveNode
{
	/**
	 * JSThisNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSThisNode(String text, int start, int end)
	{
		super(JSNodeTypes.THIS, text, start, end);
	}
}
