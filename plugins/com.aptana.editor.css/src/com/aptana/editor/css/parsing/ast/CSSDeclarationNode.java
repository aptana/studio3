package com.aptana.editor.css.parsing.ast;

import beaver.Symbol;

public class CSSDeclarationNode extends CSSNode
{

	private String fIdentifier;
	private String fStatus;
	private boolean fHasSemicolon;

	public CSSDeclarationNode(int start, int end)
	{
		super(start, end);
	}

	public CSSDeclarationNode(Symbol semicolon)
	{
		super(semicolon.getStart(), semicolon.getEnd());
		fHasSemicolon = true;
	}

	public CSSDeclarationNode(Symbol identifier, CSSExpressionNode value)
	{
		this(identifier, value, null);
	}

	public CSSDeclarationNode(Symbol identifier, CSSExpressionNode value, Symbol status)
	{
		fIdentifier = identifier.value.toString();
		fStatus = (status == null) ? null : status.value.toString();
		setChildren(new CSSNode[] { value });

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

	public CSSExpressionNode getAssignedValue()
	{
		return (CSSExpressionNode) getChild(0);
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
			text.append(": ").append(getAssignedValue()); //$NON-NLS-1$
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

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj))
		{
			return false;
		}
		if (!(obj instanceof CSSDeclarationNode))
		{
			return false;
		}
		CSSDeclarationNode otherDecl = (CSSDeclarationNode) obj;
		if (fIdentifier == null)
		{
			// if there is no identifier, it's an empty declaration, and other fields are not set either, so no need to
			// check them
			return otherDecl.fIdentifier == null;
		}
		return fIdentifier.equals(otherDecl.fIdentifier)
				&& ((fStatus == null && otherDecl.fStatus == null) || (fStatus != null && fStatus
						.equals(otherDecl.fStatus))) && fHasSemicolon == otherDecl.fHasSemicolon;
	}

	@Override
	public int hashCode()
	{
		if (fIdentifier == null)
		{
			return super.hashCode();
		}
		int hash = super.hashCode();
		hash = hash * 31 + fIdentifier.hashCode();
		hash = hash * 31 + (fStatus == null ? 0 : fStatus.hashCode());
		hash = hash * 31 + Boolean.valueOf(fHasSemicolon).hashCode();
		return hash;
	}
}
