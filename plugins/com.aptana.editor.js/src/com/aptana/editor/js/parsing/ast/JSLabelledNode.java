package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSLabelledNode extends JSNode
{
	/**
	 * JSLabelledNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSLabelledNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.LABELLED, start, end, children);
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
		buffer.append(": "); //$NON-NLS-1$
		buffer.append(children[1]);

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
