package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSGetElementOperatorNode extends JSBinaryOperatorNode
{
	/**
	 * JSGetElementOperatorNode
	 * 
	 * @param left
	 * @param right
	 */
	public JSGetElementOperatorNode(JSNode left, JSNode right)
	{
		super(left, right);
		this.end = right.getEnd() + 1; // take the end ] into account
		setType(JSNodeTypes.GET_ELEMENT);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSBinaryOperatorNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		IParseNode[] children = getChildren();
		text.append(children[0]).append("[").append(children[1]).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
		this.appendSemicolon(text);

		return text.toString();
	}
}
