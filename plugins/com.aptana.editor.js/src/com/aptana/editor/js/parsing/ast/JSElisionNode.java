package com.aptana.editor.js.parsing.ast;

public class JSElisionNode extends JSNaryNode
{
	/**
	 * JSElisionNode
	 * 
	 * @param children
	 */
	public JSElisionNode(JSNode... children)
	{
		super(JSNodeTypes.ELISION, children);
	}
}
