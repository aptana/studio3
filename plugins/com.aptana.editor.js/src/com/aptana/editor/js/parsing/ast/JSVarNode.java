package com.aptana.editor.js.parsing.ast;

public class JSVarNode extends JSNaryNode
{
	/**
	 * JSVarNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSVarNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.VAR, start, end, children);
	}
}
