/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

import java.util.List;

/**
 * CSSMsViewPort
 */
// TODO Fix the grammar rule to only accept declarations of width and height
public class CSSMsViewPort extends CSSNode
{
	private static final String MS_VIEWPORT = "@-ms-viewport "; //$NON-NLS-1$
	private static final CSSDeclarationNode[] NO_DECLARATIONS = new CSSDeclarationNode[0];

	private CSSDeclarationNode[] fDeclarations;

	/**
	 * CSSMsViewPort
	 */
	public CSSMsViewPort(List<CSSDeclarationNode> declarations)
	{
		fDeclarations = (declarations != null) ? declarations.toArray(new CSSDeclarationNode[declarations.size()])
				: NO_DECLARATIONS;
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.MS_VIEWPORT;
	}

	/**
	 * addOffset
	 */
	@Override
	public void addOffset(int offset)
	{
		super.addOffset(offset);

		for (CSSDeclarationNode node : fDeclarations)
		{
			node.addOffset(offset);
		}
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append(MS_VIEWPORT);
		text.append('{');

		CSSDeclarationNode[] declarations = getDeclarations();
		for (int i = 0; i < declarations.length; ++i)
		{
			text.append(declarations[i]);

			if (i < declarations.length - 1)
			{
				text.append(' ');
			}
		}

		text.append('}');

		return text.toString();
	}
}
