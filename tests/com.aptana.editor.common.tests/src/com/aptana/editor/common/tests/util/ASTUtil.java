/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.tests.util;

import com.aptana.core.IFilter;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.util.ParseUtil;

/**
 * ASTUtil
 */
public class ASTUtil
{
	private ASTUtil()
	{
	}

	public static void showBeforeAndAfterTrim(IParseNode node)
	{
		final int size[] = { 1, 1, 0 }; // add one to the child counts to include the root node

		IFilter<IParseNode> counter = new IFilter<IParseNode>()
		{
			public boolean include(IParseNode item)
			{
				if (item instanceof ParseNode)
				{
					size[0] += ((ParseNode) item).getChildCount();
					size[1] += ((ParseNode) item).getInternalChildCount();
					size[2]++;
				}

				return true;
			}
		};

		// show before size
		ParseUtil.treeApply(node, counter);
		System.out.println(String.format("%d/%d for %d nodes", size[0], size[1], size[2]));

		// show after size
		long before = System.currentTimeMillis();
		ParseUtil.trimToSize(node);
		long diff = System.currentTimeMillis() - before;
		System.out.println("time = " + diff + "ms");

		size[0] = 1;
		size[1] = 1;
		size[2] = 0;
		ParseUtil.treeApply(node, counter);
		System.out.println(String.format("%d/%d for %d nodes", size[0], size[1], size[2]));
	}
}
