package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSWhileNode extends JSNode
{
	/**
	 * JSWhileNode
	 * 
	 * @param children
	 */
	public JSWhileNode(JSNode... children)
	{
		super(JSNodeTypes.WHILE, children);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = getChildren();

		buffer.append("while ("); //$NON-NLS-1$
		buffer.append(children[0]);
		buffer.append(") "); //$NON-NLS-1$
		buffer.append(children[1]);

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
