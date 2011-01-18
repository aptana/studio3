/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
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
