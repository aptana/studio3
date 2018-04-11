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

public class CSSFontFaceNode extends CSSNode
{
	private static final String FONTFACE = "@font-face"; //$NON-NLS-1$

	/**
	 * CSSFontFaceNode
	 */
	public CSSFontFaceNode()
	{
		this(null);
	}

	/**
	 * CSSFontFaceNode
	 * 
	 * @param declarations
	 */
	public CSSFontFaceNode(List<CSSDeclarationNode> declarations)
	{
		if (declarations != null)
		{
			setChildren(declarations.toArray(new CSSDeclarationNode[declarations.size()]));
		}
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.FONTFACE;
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
		if (!super.equals(obj) || !(obj instanceof CSSFontFaceNode))
		{
			return false;
		}
		CSSFontFaceNode other = (CSSFontFaceNode) obj;
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

	@Override
	public String getText()
	{
		return FONTFACE;
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

		text.append(FONTFACE);
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
