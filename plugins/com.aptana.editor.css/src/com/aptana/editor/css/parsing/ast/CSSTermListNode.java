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
		if (!super.equals(obj) || !(obj instanceof CSSTermListNode))
		{
			return false;
		}
		CSSTermListNode other = (CSSTermListNode) obj;
		return toString().equals(other.toString());
	}

	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + toString().hashCode();
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
