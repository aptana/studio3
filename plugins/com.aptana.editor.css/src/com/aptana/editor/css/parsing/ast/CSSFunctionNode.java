package com.aptana.editor.css.parsing.ast;

public class CSSFunctionNode extends CSSExpressionNode
{

	public CSSFunctionNode(CSSExpressionNode expression, int start, int end)
	{
		super(start, end);
		setChildren(new CSSNode[] { expression });
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append("(").append(getChild(0)).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
		return text.toString();
	}
}
