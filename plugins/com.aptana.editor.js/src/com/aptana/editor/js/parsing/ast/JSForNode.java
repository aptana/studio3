package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSForNode extends JSNode
{
	/**
	 * JSForNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSForNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.FOR, start, end, children);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = getChildren();

		buffer.append("for ("); //$NON-NLS-1$

		if (!((JSNode) children[0]).isEmpty())
		{
			buffer.append(children[0]);
		}
		buffer.append(";"); //$NON-NLS-1$

		if (!((JSNode) children[1]).isEmpty())
		{
			buffer.append(" ").append(children[1]); //$NON-NLS-1$
		}
		buffer.append(";"); //$NON-NLS-1$

		if (!((JSNode) children[2]).isEmpty())
		{
			buffer.append(" ").append(children[2]); //$NON-NLS-1$
		}

		buffer.append(") ").append(children[3]); //$NON-NLS-1$

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
