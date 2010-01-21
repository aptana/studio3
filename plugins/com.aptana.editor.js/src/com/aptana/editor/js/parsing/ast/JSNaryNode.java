package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSNaryNode extends JSNode
{

	public JSNaryNode(short type, int start, int end)
	{
		super(type, start, end);
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		switch (getType())
		{
			case JSNodeTypes.STATEMENT:
				text.append("{"); //$NON-NLS-1$
				IParseNode[] children = getChildren();
				for (IParseNode child : children)
				{
					text.append(child).append("\n"); //$NON-NLS-1$
				}
				text.append("}"); //$NON-NLS-1$
				break;
		}
		return text.toString();
	}
}
