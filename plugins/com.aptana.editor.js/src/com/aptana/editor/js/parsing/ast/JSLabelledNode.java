package com.aptana.editor.js.parsing.ast;

public class JSLabelledNode extends JSNode
{
	/**
	 * JSLabelledNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSLabelledNode(int start, int end, JSNode ... children)
	{
		super(JSNodeTypes.LABELLED, start, end, children);
	}
}
