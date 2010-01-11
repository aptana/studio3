package com.aptana.editor.css.parsing.ast;

public class CSSAttributeSelectorNode extends CSSNode
{

	private String fAttributeText;
	private CSSExpressionNode fFuncExpr;

	public CSSAttributeSelectorNode(String text, int start, int end)
	{
		fAttributeText = text;
		this.start = start;
		this.end = end;
	}

	/**
	 * ":" + function expression
	 * 
	 * @param function
	 *            the function expression
	 */
	public CSSAttributeSelectorNode(CSSExpressionNode function, int start)
	{
		fFuncExpr = function;
		this.start = start;
		this.end = function.getEnd();
	}

	@Override
	public String toString()
	{
		if (fAttributeText == null)
		{
			return ":" + fFuncExpr.toString(); //$NON-NLS-1$
		}
		return fAttributeText;
	}
}
