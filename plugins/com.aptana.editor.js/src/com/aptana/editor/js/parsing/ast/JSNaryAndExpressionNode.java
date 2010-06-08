package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSNaryAndExpressionNode extends JSNaryNode
{
	/**
	 * JSNaryAndExpressionNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 * @param expression
	 * @param statements
	 */
	public JSNaryAndExpressionNode(short type, int start, int end, JSNode... children)
	{
		super(type, start, end, children);
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		IParseNode[] children = getChildren();
		switch (getType())
		{
			case JSNodeTypes.SWITCH:
				text.append("switch (").append(children[0]).append(") {"); //$NON-NLS-1$ //$NON-NLS-2$
				for (int i = 1; i < children.length; ++i)
				{
					text.append(children[i]);
				}
				text.append("}"); //$NON-NLS-1$
				break;
			case JSNodeTypes.CASE:
				text.append("case ").append(children[0]).append(": "); //$NON-NLS-1$ //$NON-NLS-2$
				for (int i = 1; i < children.length; ++i)
				{
					text.append(children[i]);
				}
				break;
			default:
				return super.toString();
		}

		return appendSemicolon(text.toString());
	}
}
