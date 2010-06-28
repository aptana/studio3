package com.aptana.editor.js.parsing.ast;

public class JSEmptyNode extends JSNode
{
	/**
	 * JSEmptyNode
	 */
	public JSEmptyNode(int start, int end)
	{
		super(JSNodeTypes.EMPTY, start, end);
	}
}
