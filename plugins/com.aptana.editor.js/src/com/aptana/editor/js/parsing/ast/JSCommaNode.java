package com.aptana.editor.js.parsing.ast;

public class JSCommaNode extends JSNaryNode
{
	/**
	 * JSCommaNode
	 * 
	 * @param children
	 */
	public JSCommaNode(JSNode... children)
	{
		super(JSNodeTypes.COMMA, children);
	}
}
