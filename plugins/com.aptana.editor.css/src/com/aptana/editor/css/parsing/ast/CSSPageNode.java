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

	public CSSPageNode(int start, int end)
	{
		this(null, null, start, end);
	}

	public CSSPageNode(CSSPageSelectorNode pageSelector, int start, int end)
	{
		this(pageSelector, null, start, end);
	}

	public CSSPageNode(Object declarations, int start, int end)
	{
		this(null, declarations, start, end);
	}

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

	public CSSPageSelectorNode getSelector()
	{
		return fPageSelector;
	}

	public CSSDeclarationNode[] getDeclarations()
	{
		List<IParseNode> list = Arrays.asList(getChildren());
		return list.toArray(new CSSDeclarationNode[list.size()]);
	}

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

	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + toString().hashCode();
	}

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
