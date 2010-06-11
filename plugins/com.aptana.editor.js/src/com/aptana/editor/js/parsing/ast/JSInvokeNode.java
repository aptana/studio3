package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSInvokeNode extends JSNode
{
	/**
	 * JSInvokeNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSInvokeNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.INVOKE, start, end, children);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = getChildren();

		buffer.append(children[0]);
		buffer.append(children[1]);

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
