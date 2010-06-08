package com.aptana.editor.js.parsing.ast;

public class JSTryNode extends JSNode
{
	/**
	 * JSTryNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSTryNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.TRY, start, end, children);
	}
}
