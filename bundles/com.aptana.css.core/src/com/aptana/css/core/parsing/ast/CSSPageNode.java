/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

import java.util.Arrays;
import java.util.List;

import com.aptana.parsing.ast.IParseNode;

public class CSSPageNode extends CSSNode
{

	private static final String PAGE = "@page"; //$NON-NLS-1$

	private CSSPageSelectorNode fPageSelector;

	/**
	 * CSSPageNode
	 */
	public CSSPageNode()
	{
		this(null);
	}

	/**
	 * CSSPageNode
	 * 
	 * @param declarations
	 */
	public CSSPageNode(List<CSSDeclarationNode> declarations)
	{
		if (declarations != null)
		{
			setChildren(declarations.toArray(new CSSDeclarationNode[declarations.size()]));
		}
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.PAGE;
	}

	@Override
	public void accept(CSSTreeWalker walker)
	{
		walker.visit(this);
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

	@Override
	public String getText()
	{
		StringBuilder text = new StringBuilder();
		text.append(PAGE);
		CSSPageSelectorNode selector = getSelector();
		if (selector != null)
		{
			text.append(" :").append(selector); //$NON-NLS-1$
		}
		return text.toString();
	}

	public void setSelector(CSSPageSelectorNode selector)
	{
		fPageSelector = selector;
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

		text.append(PAGE);
		CSSPageSelectorNode selector = getSelector();
		if (selector != null)
		{
			text.append(" :").append(selector); //$NON-NLS-1$
		}

		text.append(" {"); //$NON-NLS-1$
		CSSDeclarationNode[] declarations = getDeclarations();
		int size = declarations.length;
		for (int i = 0; i < size; ++i)
		{
			text.append(declarations[i]);

			if (i < size - 1)
			{
				text.append(' ');
			}
		}
		text.append('}');

		return text.toString();
	}
}
