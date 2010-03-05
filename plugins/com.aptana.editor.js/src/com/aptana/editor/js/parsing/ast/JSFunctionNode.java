package com.aptana.editor.js.parsing.ast;

public class JSFunctionNode extends JSNode
{

	public JSFunctionNode(JSNode[] children, int start, int end)
	{
		super(JSNodeTypes.FUNCTION, children, start, end);
	}

	public String getName()
	{
		return getChild(0).getText();
	}

	@Override
	public String getText()
	{
		return getName();
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append("function "); //$NON-NLS-1$
		String name = getName();
		if (name.length() > 0)
		{
			text.append(name).append(" "); //$NON-NLS-1$
		}
		text.append(getChild(1)).append(" ").append(getChild(2)); //$NON-NLS-1$
		return appendSemicolon(text.toString());
	}
}
