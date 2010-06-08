package com.aptana.editor.js.parsing.ast;

public class JSParametersNode extends JSNaryNode
{
	/**
	 * JSParametersNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSParametersNode(int start, int end, JSNode ... children)
	{
		super(JSNodeTypes.PARAMETERS, start, end, children);
	}
}
