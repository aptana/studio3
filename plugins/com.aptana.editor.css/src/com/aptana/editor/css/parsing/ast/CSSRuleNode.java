/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

import java.util.List;

public class CSSRuleNode extends CSSNode
{
	private static final CSSDeclarationNode[] NO_DECLARATIONS = new CSSDeclarationNode[0];

	private CSSSelectorNode[] fSelectors;
	private CSSDeclarationNode[] fDeclarations;

	/**
	 * CSSRuleNode
	 * 
	 * @param selectors
	 */
	public CSSRuleNode(List<CSSSelectorNode> selectors)
	{
		this(selectors, null);
	}

	/**
	 * CSSRuleNode
	 * 
	 * @param selectors
	 * @param declarations
	 */
	public CSSRuleNode(List<CSSSelectorNode> selectors, List<CSSDeclarationNode> declarations)
	{
		super(CSSNodeTypes.RULE);

		fSelectors = selectors.toArray(new CSSSelectorNode[selectors.size()]);
		fDeclarations = (declarations != null)
			? declarations.toArray(new CSSDeclarationNode[declarations.size()])
			: NO_DECLARATIONS;
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

	/**
	 * addOffset
	 */
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.ast.CSSNode#equals(java.lang.Object)
	 */
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
			{
				return false;
			}
		}

		for (int i = 0; i < fDeclarations.length; i++)
		{
			// Can't call equals() on this, because it compares parents, which is this, which results in infinite loop!
			CSSDeclarationNode otherDecl = other.fDeclarations[i];

			if (fDeclarations[i].getNodeType() != otherDecl.getNodeType())
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * getDeclarations
	 * 
	 * @return
	 */
	public CSSDeclarationNode[] getDeclarations()
	{
		return fDeclarations;
	}

	/**
	 * getSelectors
	 * 
	 * @return
	 */
	public CSSSelectorNode[] getSelectors()
	{
		return fSelectors;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#hashCode()
	 */
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		CSSSelectorNode[] selectors = getSelectors();

		for (CSSSelectorNode selector : selectors)
		{
			String combinator = selector.getCombinator();

			text.append(selector);

			if (combinator != null && combinator.length() > 0)
			{
				if (",".equals(combinator) == false)
				{
					text.append(" ");
				}

				text.append(combinator).append(" ");
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
