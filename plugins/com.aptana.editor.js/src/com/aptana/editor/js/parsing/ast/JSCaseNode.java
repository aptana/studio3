package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSCaseNode extends JSNaryAndExpressionNode
{
	/**
	 * JSCaseNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSCaseNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.CASE, start, end, children);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNaryAndExpressionNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = getChildren();

		buffer.append("case "); //$NON-NLS-1$
		buffer.append(children[0]);
		buffer.append(": "); //$NON-NLS-1$

		for (int i = 1; i < children.length; ++i)
		{
			buffer.append(children[i]);
		}

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
