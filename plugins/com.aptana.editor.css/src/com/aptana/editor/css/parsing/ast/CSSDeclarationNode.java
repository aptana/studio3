/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

import beaver.Symbol;

public class CSSDeclarationNode extends CSSNode
{
	private String fIdentifier;
	private String fStatus;
	private boolean fHasSemicolon;

	/**
	 * CSSDeclarationNode
	 * 
	 * @param start
	 * @param end
	 */
	protected CSSDeclarationNode()
	{
		super(CSSNodeTypes.DECLARATION);
	}

	/**
	 * CSSDeclarationNode
	 * 
	 * @param identifier
	 * @param value
	 */
	public CSSDeclarationNode(String identifier, CSSExpressionNode value)
	{
		this(identifier, value, null);
	}

	/**
	 * CSSDeclarationNode
	 * 
	 * @param identifier
	 * @param value
	 * @param status
	 */
	public CSSDeclarationNode(String identifier, CSSExpressionNode value, String status)
	{
		super(CSSNodeTypes.DECLARATION);

		fIdentifier = identifier;
		fStatus = status;
		
		this.setChildren(new CSSNode[] { value });
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.ast.CSSNode#accept(com.aptana.editor.css.parsing.ast.CSSTreeWalker)
	 */
	@Override
	public void accept(CSSTreeWalker walker)
	{
		walker.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.ast.CSSNode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof CSSDeclarationNode))
		{
			return false;
		}

		if (!super.equals(obj))
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
			&& ((fStatus == null && otherDecl.fStatus == null) || (fStatus != null && fStatus.equals(otherDecl.fStatus)))
			&& fHasSemicolon == otherDecl.fHasSemicolon;
	}

	/**
	 * getAssignedValue
	 * 
	 * @return
	 */
	public CSSExpressionNode getAssignedValue()
	{
		return (CSSExpressionNode) getChild(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#hashCode()
	 */
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

	/**
	 * setHasSemicolon
	 * 
	 * @param semicolon
	 */
	public void setHasSemicolon(Symbol semicolon)
	{
		fHasSemicolon = true;

		this.setLocation(this.getStart(), semicolon.getEnd());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
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
}
