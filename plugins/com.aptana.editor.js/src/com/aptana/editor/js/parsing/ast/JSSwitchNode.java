package com.aptana.editor.js.parsing.ast;

public class JSSwitchNode extends JSNaryAndExpressionNode
{
	/**
	 * JSSwitchNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSSwitchNode(int start, int end, JSNode ... children)
	{
		super(JSNodeTypes.SWITCH, start, end, children);
	}
}
