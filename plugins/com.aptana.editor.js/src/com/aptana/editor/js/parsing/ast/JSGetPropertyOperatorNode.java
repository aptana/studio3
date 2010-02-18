package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSGetPropertyOperatorNode extends JSBinaryOperatorNode
{

	public JSGetPropertyOperatorNode(JSNode left, JSNode right)
	{
		super(left, right);
		setType(JSNodeTypes.GET_PROPERTY);
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		IParseNode[] children = getChildren();
		text.append(children[0]).append(".").append(children[1]); //$NON-NLS-1$
		return appendSemicolon(text.toString());
	}
}
