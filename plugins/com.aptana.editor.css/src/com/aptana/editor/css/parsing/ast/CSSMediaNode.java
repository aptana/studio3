/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

import java.util.Arrays;

public class CSSMediaNode extends CSSNode
{

	private CSSTextNode[] fMedias;
	private CSSNode[] fStatements;
	private String fText;

	public CSSMediaNode(CSSTextNode[] medias, int start, int end)
	{
		this(medias, new CSSNode[0], start, end);
	}

	public CSSMediaNode(CSSTextNode[] medias, CSSNode[] statements, int start, int end)
	{
		super(CSSNodeTypes.MEDIA, start, end);
		fMedias = medias;
		fStatements = statements;
	}

	public CSSTextNode[] getMedias()
	{
		return fMedias;
	}

	public CSSNode[] getStatements()
	{
		return fStatements;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof CSSMediaNode))
		{
			return false;
		}
		CSSMediaNode other = (CSSMediaNode) obj;
		return Arrays.equals(fMedias, other.fMedias) && Arrays.equals(fStatements, other.fStatements);
	}

	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
		hash = hash * 31 + Arrays.hashCode(fMedias);
		hash = hash * 31 + Arrays.hashCode(fStatements);
		return hash;
	}

	@Override
	public String toString()
	{
		if (fText == null)
		{
			StringBuilder text = new StringBuilder();
			text.append("@media"); //$NON-NLS-1$
			for (CSSTextNode media : fMedias)
			{
				text.append(" ").append(media); //$NON-NLS-1$
			}
			text.append("{"); //$NON-NLS-1$
			for (CSSNode statement : fStatements)
			{
				text.append(statement);
			}
			text.append("}"); //$NON-NLS-1$
			fText = text.toString();
		}
		return fText;
	}
}
