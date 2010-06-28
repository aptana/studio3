package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSTryNode extends JSNode
{
	/**
	 * JSTryNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSTryNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.TRY, start, end, children);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = getChildren();

		buffer.append("try "); //$NON-NLS-1$
		buffer.append(children[0]);

		if (!((JSNode) children[1]).isEmpty())
		{
			buffer.append(" ").append(children[1]); //$NON-NLS-1$
		}

		if (!((JSNode) children[2]).isEmpty())
		{
			buffer.append(" ").append(children[2]); //$NON-NLS-1$
		}

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
