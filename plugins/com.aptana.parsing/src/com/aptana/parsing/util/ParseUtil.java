/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.util;

import java.util.LinkedList;
import java.util.Queue;

import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseRootNode;

/**
 * ParseUtil
 */
public class ParseUtil
{
	private ParseUtil()
	{
	}

	public static void addOffset(IParseNode node, int offset)
	{
		addOffset(node, offset, true);
	}

	public static void addOffset(IParseNode node, int offset, boolean recursive)
	{
		Queue<IParseNode> queue = new LinkedList<IParseNode>();

		// prime queue
		queue.offer(node);

		while (!queue.isEmpty())
		{
			IParseNode current = queue.poll();

			if (current instanceof ParseNode)
			{
				((ParseNode) current).addOffset(offset);
			}

			if (recursive)
			{
				if (current instanceof ParseRootNode)
				{
					for (IParseNode commentNode : ((ParseRootNode) node).getCommentNodes())
					{
						queue.offer(commentNode);
					}
				}

				for (IParseNode child : current)
				{
					queue.offer(child);
				}
			}
		}
	}
}
