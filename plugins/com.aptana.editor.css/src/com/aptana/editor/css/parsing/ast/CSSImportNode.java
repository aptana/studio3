/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

import java.util.Arrays;

public class CSSImportNode extends CSSNode
{
	private String fUriStr;
	private CSSTextNode[] fMediaList;

	/**
	 * CSSImportNode
	 * 
	 * @param uri
	 * @param mediaList
	 */
	public CSSImportNode(String uri, CSSTextNode... mediaList)
	{
		super(CSSNodeTypes.IMPORT);

		fUriStr = uri;
		fMediaList = mediaList;
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
		if (!super.equals(obj) || !(obj instanceof CSSImportNode))
		{
			return false;
		}

		CSSImportNode other = (CSSImportNode) obj;

		return fUriStr.equals(other.fUriStr) && Arrays.equals(fMediaList, other.fMediaList);
	}

	/**
	 * getMedias
	 * 
	 * @return
	 */
	public CSSTextNode[] getMedias()
	{
		return fMediaList;
	}

	/**
	 * getUri
	 * 
	 * @return
	 */
	public String getUri()
	{
		return fUriStr;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = super.hashCode();

		hash = hash * 31 + fUriStr.hashCode();
		hash = hash * 31 + Arrays.hashCode(fMediaList);

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

		text.append("@import ").append(fUriStr); //$NON-NLS-1$

		for (int i = 0; i < fMediaList.length; ++i)
		{
			text.append(" ").append(fMediaList[i]); //$NON-NLS-1$

			if (i < fMediaList.length - 1)
			{
				text.append(","); //$NON-NLS-1$
			}
		}

		text.append(";"); //$NON-NLS-1$

		return text.toString();
	}
}
