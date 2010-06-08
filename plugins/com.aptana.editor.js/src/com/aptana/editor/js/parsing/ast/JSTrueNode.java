package com.aptana.editor.js.parsing.ast;

public class JSTrueNode extends JSPrimitiveNode
{
	/**
	 * JSTrueNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSTrueNode(int start, int end)
	{
		super(JSNodeTypes.TRUE, start, end, "true");
	}
}
