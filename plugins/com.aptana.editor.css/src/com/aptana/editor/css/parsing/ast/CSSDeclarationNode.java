package com.aptana.editor.css.parsing.ast;

import beaver.Symbol;

public class CSSDeclarationNode extends CSSNode
{

	private String fIdentifier;
	private CSSExpressionNode fValue;
	private String fStatus;
	private boolean fHasSemicolon;

	public CSSDeclarationNode()
	{
	}

	public CSSDeclarationNode(Symbol semicolon)
	{
		fHasSemicolon = true;
		this.start = semicolon.getStart();
		this.end = semicolon.getEnd();
	}

	public CSSDeclarationNode(Symbol identifier, CSSExpressionNode value)
	{
		this(identifier, value, null);
	}

	public CSSDeclarationNode(Symbol identifier, CSSExpressionNode value, Symbol status)
	{
		fIdentifier = identifier.value.toString();
		fValue = value;
		fStatus = (status == null) ? null : status.value.toString();

		this.start = identifier.getStart();
		if (status == null)
		{
			this.end = value.getEnd();
		}
		else
		{
			this.end = status.getEnd();
		}
	}

	public void setHasSemicolon(Symbol semicolon)
	{
		fHasSemicolon = true;
		this.end = semicolon.getEnd();
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		if (fIdentifier != null)
		{
			text.append(fIdentifier);
			text.append(": ").append(fValue); //$NON-NLS-1$
			if (fStatus != null)
			{
				text.append(" ").append(fStatus); //$NON-NLS-1$
			}
		}
		if (fHasSemicolon)
		{
			text.append(";"); //$NON-NLS-1$
		}
		return text.toString();
	}
}
