package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSForInNode extends JSNode
{
	/**
	 * JSForInNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSForInNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.FOR_IN, start, end, children);
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
		buffer.append(children[0]);
		buffer.append(" in "); //$NON-NLS-1$
		buffer.append(children[1]).append(") "); //$NON-NLS-1$
		buffer.append(children[2]);

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
