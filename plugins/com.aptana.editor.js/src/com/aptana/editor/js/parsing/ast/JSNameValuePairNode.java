package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSNameValuePairNode extends JSNode
{
	/**
	 * JSNameValuePairNode
	 * 
	 * @param children
	 */
	public JSNameValuePairNode(JSNode... children)
	{
		super(JSNodeTypes.NAME_VALUE_PAIR, children);
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
