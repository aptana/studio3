package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSConditionalNode extends JSNode
{
	/**
	 * JSConditionalNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSConditionalNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.CONDITIONAL, start, end, children);
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
		buffer.append(" ? "); //$NON-NLS-1$
		buffer.append(children[1]);
		buffer.append(" : "); //$NON-NLS-1$
		buffer.append(children[2]);

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
