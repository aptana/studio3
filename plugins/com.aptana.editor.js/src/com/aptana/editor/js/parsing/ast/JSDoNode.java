package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSDoNode extends JSNode
{
	/**
	 * JSDoNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSDoNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.DO, start, end, children);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = getChildren();

		buffer.append("do ").append(children[0]); //$NON-NLS-1$

		if (children[0].getNodeType() != JSNodeTypes.STATEMENTS)
		{
			buffer.append(";"); //$NON-NLS-1$
		}

		buffer.append(" while (").append(children[1]).append(")"); //$NON-NLS-1$ //$NON-NLS-2$

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
