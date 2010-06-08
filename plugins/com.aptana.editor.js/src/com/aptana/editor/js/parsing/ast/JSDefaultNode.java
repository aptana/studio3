package com.aptana.editor.js.parsing.ast;

public class JSDefaultNode extends JSNaryNode
{
	/**
	 * JSDefaultNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSDefaultNode(int start, int end, JSNode ... children)
	{
		super(JSNodeTypes.DEFAULT, start, end, children);
	}
}
