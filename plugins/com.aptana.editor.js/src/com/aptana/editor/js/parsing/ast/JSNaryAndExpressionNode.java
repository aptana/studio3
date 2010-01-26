package com.aptana.editor.js.parsing.ast;

import java.util.ArrayList;
import java.util.List;

import com.aptana.parsing.ast.IParseNode;

public class JSNaryAndExpressionNode extends JSNaryNode
{

	public JSNaryAndExpressionNode(short type, JSNode expression, int start, int end)
	{
		this(type, expression, new JSNode[0], start, end);
	}

	public JSNaryAndExpressionNode(short type, JSNode expression, JSNode[] statements, int start, int end)
	{
		super(type, start, end);
		List<JSNode> children = new ArrayList<JSNode>();
		children.add(expression);
		for (JSNode statement : statements)
		{
			children.add(statement);
		}
		setChildren(children.toArray(new JSNode[children.size()]));
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
