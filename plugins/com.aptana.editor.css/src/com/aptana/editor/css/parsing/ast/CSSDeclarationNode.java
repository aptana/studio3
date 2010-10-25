/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

import beaver.Symbol;

public class CSSDeclarationNode extends CSSNode
{

	private String fIdentifier;
	private String fStatus;
	private boolean fHasSemicolon;

	public CSSDeclarationNode(int start, int end)
	{
		super(CSSNodeTypes.DECLARATION, start, end);
	}

	public CSSDeclarationNode(Symbol semicolon)
	{
		super(CSSNodeTypes.DECLARATION, semicolon.getStart(), semicolon.getEnd());
		fHasSemicolon = true;
	}

	public CSSDeclarationNode(Symbol identifier, CSSExpressionNode value)
	{
		this(identifier, value, null);
	}

	public CSSDeclarationNode(Symbol identifier, CSSExpressionNode value, Symbol status)
	{
		super(CSSNodeTypes.DECLARATION);
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
