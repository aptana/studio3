package com.aptana.editor.js.parsing.ast;


public abstract class JSNaryNode extends JSNode
{
	/**
	 * JSNaryNode
	 * 
	 * @param type
	 * @param children
	 */
	public JSNaryNode(short type, JSNode... children)
	{
		super(type, children);
	}
}
