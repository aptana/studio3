package com.aptana.editor.js.parsing.ast;

public class JSArgumentsNode extends JSNaryNode
{
	/**
	 * JSArgumentsNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSArgumentsNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.ARGUMENTS, start, end, children);
	}
}
