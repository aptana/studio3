package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSIfNode extends JSNode
{
	/**
	 * JSIfNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSIfNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.IF, start, end, children);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = getChildren();

		buffer.append("if (").append(children[0]).append(") "); //$NON-NLS-1$ //$NON-NLS-2$
		buffer.append(children[1]);

		if (!((JSNode) children[2]).isEmpty())
		{
			if (children[1].getNodeType() != JSNodeTypes.STATEMENTS)
			{
				buffer.append(";"); //$NON-NLS-1$
			}

			buffer.append(" else ").append(children[2]); //$NON-NLS-1$
		}

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
