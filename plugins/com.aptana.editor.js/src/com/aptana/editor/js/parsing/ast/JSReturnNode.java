package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSReturnNode extends JSUnaryOperatorNode
{
	/**
	 * JSReturnNode
	 * 
	 * @param start
	 * @param end
	 * @param expression
	 */
	public JSReturnNode(int start, int end, JSNode expression)
	{
		super(JSNodeTypes.RETURN, start, end, expression);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSUnaryOperatorNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = this.getChildren();

		buffer.append("return"); //$NON-NLS-1$

		if (!children[0].isEmpty())
		{
			buffer.append(" ");
			buffer.append(children[0]);
		}

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
