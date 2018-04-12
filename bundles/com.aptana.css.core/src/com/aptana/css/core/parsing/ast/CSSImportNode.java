/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

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
		fUriStr = uri;
		fMediaList = mediaList;
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.IMPORT;
	}

	@Override
	public void accept(CSSTreeWalker walker)
	{
		walker.visit(this);
	}

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

	@Override
	public int hashCode()
	{
		int hash = super.hashCode();

		hash = hash * 31 + fUriStr.hashCode();
		hash = hash * 31 + Arrays.hashCode(fMediaList);

		return hash;
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();

		text.append("@import ").append(getUri()); //$NON-NLS-1$

		CSSTextNode[] medias = getMedias();
		for (int i = 0; i < medias.length; ++i)
		{
			text.append(' ').append(medias[i]);

			if (i < medias.length - 1)
			{
				text.append(',');
			}
		}
		text.append(';');

		return text.toString();
	}
}
