package com.aptana.editor.js.parsing.ast;

public class JSThisNode extends JSPrimitiveNode
{
	/**
	 * JSThisNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSThisNode(int start, int end)
	{
		super(JSNodeTypes.THIS, start, end, "this");
	}
}
