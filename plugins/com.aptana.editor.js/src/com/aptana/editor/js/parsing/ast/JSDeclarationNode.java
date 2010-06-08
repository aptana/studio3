package com.aptana.editor.js.parsing.ast;

public class JSDeclarationNode extends JSNode
{
	/**
	 * JSDeclarationNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSDeclarationNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.DECLARATION, start, end, children);
	}
}
