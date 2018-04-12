/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.svg.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.css.core.ICSSConstants;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.common.outline.CompositeOutlineContentProvider;
import com.aptana.editor.css.outline.CSSOutlineContentProvider;
import com.aptana.editor.js.outline.JSOutlineContentProvider;
import com.aptana.js.core.IJSConstants;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.xml.core.parsing.ast.XMLElementNode;

/**
 * SVGOutlineContentProvider
 */
public class SVGOutlineContentProvider extends CompositeOutlineContentProvider
{
	/**
	 * SVGOutlineContentProvider
	 */
	public SVGOutlineContentProvider()
	{
		this.addSubLanguage(IJSConstants.CONTENT_TYPE_JS, new JSOutlineContentProvider());
		this.addSubLanguage(ICSSConstants.CONTENT_TYPE_CSS, new CSSOutlineContentProvider());
	}

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		List<CommonOutlineItem> items = new ArrayList<CommonOutlineItem>();
		for (IParseNode node : nodes)
		{
			if (node instanceof XMLElementNode)
			{
				items.add(getOutlineItem(node));
			}
		}
		return items.toArray(new CommonOutlineItem[items.size()]);
	}
}
