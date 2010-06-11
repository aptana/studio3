package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSThrowNode extends JSUnaryOperatorNode
{
	/**
	 * JSThrowNode
	 * 
	 * @param start
	 * @param end
	 * @param expression
	 */
	public JSThrowNode(int start, int end, JSNode expression)
	{
		super(JSNodeTypes.THROW, start, end, expression);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSUnaryOperatorNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = this.getChildren();

		buffer.append("throw "); //$NON-NLS-1$
		buffer.append(children[0]);

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
