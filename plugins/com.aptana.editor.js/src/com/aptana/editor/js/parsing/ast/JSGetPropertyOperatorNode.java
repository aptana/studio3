package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSGetPropertyOperatorNode extends JSBinaryOperatorNode
{
	/**
	 * JSGetPropertyOperatorNode
	 * 
	 * @param left
	 * @param right
	 */
	public JSGetPropertyOperatorNode(JSNode left, JSNode right)
	{
		super(left, right);
		setType(JSNodeTypes.GET_PROPERTY);
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
		text.append(children[0]).append(".").append(children[1]); //$NON-NLS-1$
		this.appendSemicolon(text);

		return text.toString();
	}
}
