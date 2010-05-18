package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSNaryNode extends JSNode
{

	public JSNaryNode(short type, int start, int end)
	{
		super(type, start, end);
	}

	public JSNaryNode(short type, JSNode[] children, int start, int end)
	{
		super(type, children, start, end);
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		IParseNode[] children = getChildren();
		switch (getType())
		{
			case JSNodeTypes.STATEMENTS:
				text.append("{"); //$NON-NLS-1$
				for (IParseNode child : children)
				{
					text.append(child);
				}
				text.append("}"); //$NON-NLS-1$
				break;
			case JSNodeTypes.VAR:
				text.append("var "); //$NON-NLS-1$
				appendText(text, children);
				break;
			case JSNodeTypes.PARAMETERS:
				text.append("("); //$NON-NLS-1$
				appendText(text, children);
				text.append(")"); //$NON-NLS-1$
				break;
			case JSNodeTypes.ARRAY_LITERAL:
				text.append("["); //$NON-NLS-1$
				appendText(text, children);
				text.append("]"); //$NON-NLS-1$
				break;
			case JSNodeTypes.OBJECT_LITERAL:
				text.append("{"); //$NON-NLS-1$
				appendText(text, children);
				text.append("}"); //$NON-NLS-1$
				break;
			case JSNodeTypes.DEFAULT:
				text.append("default: "); //$NON-NLS-1$
				for (IParseNode child : children)
				{
					text.append(child);
				}
				break;
			case JSNodeTypes.ARGUMENTS:
				text.append("(");
				appendText(text, children);
				text.append(")");
				break;
			default:
				appendText(text, children);
		}

		return appendSemicolon(text.toString());
	}

	private static void appendText(StringBuilder text, IParseNode[] children)
	{
		int count = children.length;
		for (int i = 0; i < count; ++i)
		{
			text.append(children[i]);
			if (i < count - 1)
			{
				text.append(", "); //$NON-NLS-1$
			}
		}
	}
}
