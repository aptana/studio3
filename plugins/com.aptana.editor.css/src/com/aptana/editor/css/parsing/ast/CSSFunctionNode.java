package com.aptana.editor.css.parsing.ast;

public class CSSFunctionNode extends CSSExpressionNode
{

	public CSSFunctionNode(CSSExpressionNode expression, int start, int end)
	{
		super(start, end);
		setChildren(new CSSNode[] { expression });
	}

	public CSSExpressionNode getExpression()
	{
		return (CSSExpressionNode) getChild(0);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof CSSFunctionNode))
		{
			return false;
		}
		CSSFunctionNode other = (CSSFunctionNode) obj;
		return getExpression().equals(other.getExpression());
	}

	@Override
	public int hashCode()
	{
		return getExpression().hashCode();
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append("(").append(getExpression()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
		return text.toString();
	}
}
