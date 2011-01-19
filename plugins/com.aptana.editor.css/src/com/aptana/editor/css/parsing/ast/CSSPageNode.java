/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

import java.util.Arrays;
import java.util.List;

import com.aptana.parsing.ast.IParseNode;

public class CSSPageNode extends CSSNode
{
	private CSSPageSelectorNode fPageSelector;

	/**
	 * CSSPageNode
	 * 
	 * @param pageSelector
	 * @param start
	 * @param end
	 */
	public CSSPageNode(CSSPageSelectorNode pageSelector, int start, int end)
	{
		this(pageSelector, null, start, end);
	}

	/**
	 * CSSPageNode
	 * 
	 * @param pageSelector
	 * @param declarations
	 * @param start
	 * @param end
	 */
	@SuppressWarnings("unchecked")
	public CSSPageNode(CSSPageSelectorNode pageSelector, Object declarations, int start, int end)
	{
		super(CSSNodeTypes.PAGE, start, end);

		fPageSelector = pageSelector;

		if (declarations instanceof CSSDeclarationNode)
		{
			setChildren(new CSSDeclarationNode[] { (CSSDeclarationNode) declarations });
		}
		else if (declarations instanceof List<?>)
		{
			List<CSSDeclarationNode> list = (List<CSSDeclarationNode>) declarations;

			setChildren(list.toArray(new CSSDeclarationNode[list.size()]));
		}
	}

	/**
	 * CSSPageNode
	 * 
	 * @param start
	 * @param end
	 */
	public CSSPageNode(int start, int end)
	{
		this(null, null, start, end);
	}

	/**
	 * CSSPageNode
	 * 
	 * @param declarations
	 * @param start
	 * @param end
	 */
	public CSSPageNode(Object declarations, int start, int end)
	{
		this(null, declarations, start, end);
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
		if (!super.equals(obj) || !(obj instanceof CSSPageNode))
		{
			return false;
		}

		CSSPageNode other = (CSSPageNode) obj;

		return toString().equals(other.toString());
	}

	/**
	 * getDeclarations
	 * 
	 * @return
	 */
	public CSSDeclarationNode[] getDeclarations()
	{
		List<IParseNode> list = Arrays.asList(getChildren());

		return list.toArray(new CSSDeclarationNode[list.size()]);
	}

	/**
	 * getSelector
	 * 
	 * @return
	 */
	public CSSPageSelectorNode getSelector()
	{
		return fPageSelector;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + toString().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();

		text.append("@page "); //$NON-NLS-1$

		if (fPageSelector != null)
		{
			text.append(":").append(fPageSelector).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
		}

		text.append("{"); //$NON-NLS-1$

		CSSDeclarationNode[] declarations = getDeclarations();
		int size = declarations.length;

		for (int i = 0; i < size; ++i)
		{
			text.append(declarations[i]);

			if (i < size - 1)
			{
				text.append(" "); //$NON-NLS-1$
			}
		}

		text.append("}"); //$NON-NLS-1$

		return text.toString();
	}
}
