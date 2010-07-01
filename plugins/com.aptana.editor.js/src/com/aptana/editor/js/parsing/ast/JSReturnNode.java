package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSReturnNode extends JSPreUnaryOperatorNode
{
	/**
	 * JSReturnNode
	 * 
	 * @param expression
	 */
	public JSReturnNode(JSNode expression)
	{
		super(JSNodeTypes.RETURN, expression);
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
