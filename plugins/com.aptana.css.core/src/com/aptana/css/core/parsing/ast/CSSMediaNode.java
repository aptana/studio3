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

public class CSSMediaNode extends CSSNode
{
	private static final String MEDIA = "@media"; //$NON-NLS-1$

	private CSSTextNode[] fMedias;
	private String fText;

	/**
	 * CSSMediaNode
	 * 
	 * @param medias
	 * @param statements
	 */
	public CSSMediaNode(CSSTextNode[] medias, CSSNode... statements)
	{
		fMedias = medias;
		setChildren(statements);
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.MEDIA;
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
		if (!super.equals(obj) || !(obj instanceof CSSMediaNode))
		{
			return false;
		}
		CSSMediaNode other = (CSSMediaNode) obj;
		return toString().equals(other.toString());
	}

	/**
	 * getMedias
	 * 
	 * @return
	 */
	public CSSTextNode[] getMedias()
	{
		return fMedias;
	}

	/**
	 * getStatements
	 * 
	 * @return
	 */
	public CSSNode[] getStatements()
	{
		List<IParseNode> list = Arrays.asList(getChildren());
		return list.toArray(new CSSNode[list.size()]);
	}

	@Override
	public String getText()
	{
		StringBuilder text = new StringBuilder();
		text.append(MEDIA);
		for (CSSTextNode media : fMedias)
		{
			text.append(' ').append(media);
		}
		return text.toString();
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

	@Override
	public String toString()
	{
		if (fText == null)
		{
			StringBuilder text = new StringBuilder();
			text.append(MEDIA);
			for (CSSTextNode media : fMedias)
			{
				text.append(' ').append(media);
			}

			text.append('{');
			CSSNode[] statements = getStatements();
			for (CSSNode statement : statements)
			{
				text.append(statement);
			}
			text.append('}');

			fText = text.toString();
		}

		return fText;
	}
}
