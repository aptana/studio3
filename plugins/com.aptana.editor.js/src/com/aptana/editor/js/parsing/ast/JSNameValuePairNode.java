package com.aptana.editor.js.parsing.ast;

public class JSNameValuePairNode extends JSNode
{
	/**
	 * JSNameValuePairNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSNameValuePairNode(int start, int end, JSNode ... children)
	{
		super(JSNodeTypes.NAME_VALUE_PAIR, start, end, children);
	}
}
