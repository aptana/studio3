/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.outline;

import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.json.core.parsing.ast.JSONObjectNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * JSONOutlineContentProvider
 */
public class JSONOutlineContentProvider extends CommonOutlineContentProvider
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.outline.CommonOutlineContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof JSONObjectNode)
		{
			JSONObjectNode entry = (JSONObjectNode) parentElement;
			Object[] children = new Object[entry.getChildCount()];

			for (int i = 0; i < children.length; i++)
			{
				IParseNode child = entry.getChild(i);
				CommonOutlineItem item = new CommonOutlineItem(child.getFirstChild(), child.getLastChild());
				
				children[i] = item;
			}

			return children;
		}
		else
		{
			return super.getChildren(parentElement);
		}
	}
}
