package com.aptana.editor.css.parsing.ast;

public class CSSAttributeSelectorNode extends CSSNode
{

	private String fAttributeText;

	public CSSAttributeSelectorNode(String text, int start, int end)
	{
		super(start, end);
		fAttributeText = text;
	}

	/**
	 * ":" + function expression
	 * 
	 * @param function
	 *            the function expression
	 */
	public CSSAttributeSelectorNode(CSSExpressionNode function, int start)
	{
		super(start, function.getEnd());
		setChildren(new CSSNode[] { function });
	}

	public CSSExpressionNode getFunction()
	{
		return (CSSExpressionNode) getChild(0);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof CSSAttributeSelectorNode))
		{
			return false;
		}
		CSSAttributeSelectorNode other = (CSSAttributeSelectorNode) obj;
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
		if (fAttributeText == null)
		{
			return ":" + getFunction(); //$NON-NLS-1$
		}
		return fAttributeText;
	}
}
