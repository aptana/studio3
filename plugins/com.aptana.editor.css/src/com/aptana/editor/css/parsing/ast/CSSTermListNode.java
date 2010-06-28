package com.aptana.editor.css.parsing.ast;

public class CSSTermListNode extends CSSExpressionNode
{

	private String fSeparator;

	public CSSTermListNode(CSSExpressionNode left, CSSExpressionNode right)
	{
		this(left, right, null);
	}

	public CSSTermListNode(CSSExpressionNode left, CSSExpressionNode right, String separator)
	{
		super(left.getStart(), right.getEnd());
		setChildren(new CSSExpressionNode[] { left, right });
		fSeparator = separator;
	}

	public CSSExpressionNode getLeftExpression()
	{
		return (CSSExpressionNode) getChild(0);
	}

	public CSSExpressionNode getRightExpression()
	{
		return (CSSExpressionNode) getChild(1);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof CSSTermListNode))
		{
			return false;
		}
		CSSTermListNode other = (CSSTermListNode) obj;
		return getLeftExpression().equals(other.getLeftExpression())
				&& (fSeparator == null ? other.fSeparator == null : fSeparator.equals(other.fSeparator))
				&& getRightExpression().equals(other.getRightExpression());
	}

	@Override
	public int hashCode()
	{
		int hash = getLeftExpression().hashCode();
		hash = 31 * hash + (fSeparator == null ? 0 : fSeparator.hashCode());
		hash = 31 * hash + getRightExpression().hashCode();
		return hash;
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append(getLeftExpression());
		if (fSeparator == null)
		{
			text.append(" "); //$NON-NLS-1$
		}
		else
		{
			text.append(fSeparator);
		}
		text.append(getRightExpression());
		return text.toString();
	}
}
