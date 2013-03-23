/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

import beaver.Symbol;

import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class CSSDeclarationNode extends CSSNode
{
	private final String fIdentifier;
	private final String fStatus;
	// Memory-optimization: only store start/end and create Range when needed.
	private final int fStatusStart;
	private final int fStatusEnd;
	private boolean fHasSemicolon;

	/**
	 * CSSDeclarationNode
	 */
	protected CSSDeclarationNode()
	{
		fStatus = null;
		fIdentifier = null;
		fStatusStart = 0;
		fStatusEnd = -1;
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
	public CSSDeclarationNode(String identifier, CSSExpressionNode value, Symbol status)
	{
		fIdentifier = identifier;

		if (status != null)
		{
			fStatus = status.value.toString();
			fStatusStart = status.getStart();
			fStatusEnd = status.getEnd();
		}
		else
		{
			fStatusStart = 0;
			fStatusEnd = -1;
			fStatus = null;
		}

		setChildren(new CSSNode[] { value });
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.DECLARATION;
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
		if (!(obj instanceof CSSDeclarationNode) || !super.equals(obj))
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

	/**
	 * getAssignedValue
	 * 
	 * @return
	 */
	public CSSExpressionNode getAssignedValue()
	{
		return (CSSExpressionNode) getChild(0);
	}

	/**
	 * getIdentifier
	 * 
	 * @return
	 */
	public String getIdentifier()
	{
		return fIdentifier;
	}

	/**
	 * getStatus
	 * 
	 * @return String or null
	 */
	public String getStatus()
	{
		return fStatus;
	}

	/**
	 * getStatusRange
	 * 
	 * @return
	 */
	public IRange getStatusRange()
	{
		return (fStatus != null) ? new Range(fStatusStart, fStatusEnd) : Range.EMPTY;
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
		hash = hash * 31 + ((fStatus == null) ? 0 : fStatus.hashCode());
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
				text.append(' ').append(fStatus);
			}
		}

		if (fHasSemicolon)
		{
			text.append(';');
		}

		return text.toString();
	}
}
