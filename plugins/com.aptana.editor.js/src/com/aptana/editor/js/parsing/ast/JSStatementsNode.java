package com.aptana.editor.js.parsing.ast;

public class JSStatementsNode extends JSNaryNode
{
	/**
	 * JSStatementsNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSStatementsNode(int start, int end, JSNode ... children)
	{
		super(JSNodeTypes.STATEMENTS, start, end, children);
	}
}
