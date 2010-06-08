package com.aptana.editor.js.parsing.ast;

public class JSTrueNode extends JSPrimitiveNode
{
	/**
	 * JSTrueNode
	 * 
	 * @param text
	 * @param start
	 * @param end
	 */
	public JSTrueNode(String text, int start, int end)
	{
		super(JSNodeTypes.TRUE, text, start, end);
	}
}
