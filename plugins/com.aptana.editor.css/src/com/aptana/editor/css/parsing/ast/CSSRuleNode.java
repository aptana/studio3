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

import java.util.List;

import beaver.Symbol;

public class CSSRuleNode extends CSSNode
{

	private CSSSelectorNode[] fSelectors;
	private CSSDeclarationNode[] fDeclarations;

	public CSSRuleNode(Symbol[] selectors, int end)
	{
		this(selectors, null, end);
	}

	@SuppressWarnings("unchecked")
	public CSSRuleNode(Symbol[] selectors, Object declarations, int end)
	{
		super(CSSNodeTypes.RULE);
		fSelectors = new CSSSelectorNode[selectors.length];
		List<CSSSimpleSelectorNode> simpleSelectors;
		for (int i = 0; i < selectors.length; ++i)
		{
			simpleSelectors = (List<CSSSimpleSelectorNode>) selectors[i].value;
			fSelectors[i] = new CSSSelectorNode(this, simpleSelectors.toArray(new CSSSimpleSelectorNode[simpleSelectors
					.size()]), selectors[i].getStart(), selectors[i].getEnd());
		}
		if (selectors.length > 0)
		{
			this.start = selectors[0].getStart();
		}

		if (declarations instanceof CSSDeclarationNode)
		{
			fDeclarations = new CSSDeclarationNode[1];
			fDeclarations[0] = (CSSDeclarationNode) declarations;
		}
		else if (declarations instanceof List<?>)
		{
			List<CSSDeclarationNode> list = (List<CSSDeclarationNode>) declarations;
			int size = list.size();
			fDeclarations = new CSSDeclarationNode[size];
			for (int i = 0; i < size; ++i)
			{
				fDeclarations[i] = list.get(i);
			}
		}
		else
		{
			fDeclarations = new CSSDeclarationNode[0];
		}
		if (fSelectors.length > 0)
		{
			for (CSSDeclarationNode declaration : fDeclarations)
			{
				declaration.setParent(fSelectors[0]);
			}
		}
		this.end = end;
	}

	public CSSSelectorNode[] getSelectors()
	{
		return fSelectors;
	}

	public CSSDeclarationNode[] getDeclarations()
	{
		return fDeclarations;
	}

	@Override
	public void addOffset(int offset)
	{
		super.addOffset(offset);
		for (CSSSelectorNode node : fSelectors)
		{
			node.addOffset(offset);
		}
		for (CSSDeclarationNode node : fDeclarations)
		{
			node.addOffset(offset);
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof CSSRuleNode) || !super.equals(obj))
		{
			return false;
		}
		CSSRuleNode other = (CSSRuleNode) obj;
		if (fDeclarations.length != other.fDeclarations.length)
		{
			return false;
		}
		if (fSelectors.length != other.fSelectors.length)
		{
			return false;
		}
		for (int i = 0; i < fSelectors.length; i++)
		{
			// Can't call equals() on this, because it compares parents, which is this, which results in infinite loop!
			CSSSelectorNode otherSelector = other.fSelectors[i];
			if (fSelectors[i].getNodeType() != otherSelector.getNodeType())
				return false;
		}
		for (int i = 0; i < fDeclarations.length; i++)
		{
			// Can't call equals() on this, because it compares parents, which is this, which results in infinite loop!
			CSSDeclarationNode otherDecl = other.fDeclarations[i];
			if (fDeclarations[i].getNodeType() != otherDecl.getNodeType())
				return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
		for (CSSSelectorNode node : fSelectors)
		{
			hash = hash * 31 + node.hashCode();
		}
		for (CSSDeclarationNode node : fDeclarations)
		{
			hash = hash * 31 + node.hashCode();
		}
		return hash;
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		CSSSelectorNode[] selectors = getSelectors();
		for (int i = 0; i < selectors.length; ++i)
		{
			text.append(selectors[i]);
			if (i < selectors.length - 1)
			{
				text.append(", "); //$NON-NLS-1$
			}
		}

		CSSDeclarationNode[] declarations = getDeclarations();
		text.append(" {"); //$NON-NLS-1$
		for (int i = 0; i < declarations.length; ++i)
		{
			text.append(declarations[i]);
			if (i < declarations.length - 1)
			{
				text.append(" "); //$NON-NLS-1$
			}
		}
		text.append("}"); //$NON-NLS-1$

		return text.toString();
	}
}
