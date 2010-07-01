package com.aptana.editor.js.parsing.ast;

public class JSElementsNode extends JSNaryNode
{
	/**
	 * JSElementsNode
	 * 
	 * @param children
	 */
	public JSElementsNode(JSNode... children)
	{
		super(JSNodeTypes.ELEMENTS, children);
	}
}
