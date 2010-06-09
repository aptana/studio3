package com.aptana.editor.js.parsing.ast;

public class JSErrorNode extends JSNode
{
	/**
	 * JSErrorNode
	 */
	public JSErrorNode(int start, int end)
	{
		super(JSNodeTypes.ERROR, start, end);
	}
}
