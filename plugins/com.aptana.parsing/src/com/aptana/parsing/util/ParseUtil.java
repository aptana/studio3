/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.aptana.core.util.StringUtil;
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

	/**
	 * Move the starting and ending offsets of the specified node (and its descendants) by the specified amount.
	 * 
	 * @param node
	 * @param offset
	 */
	public static void addOffset(IParseNode node, int offset)
	{
		addOffset(node, offset, true);
	}

	/**
	 * Move the starting and ending offsets of the specified node by the specified amount. If the recursive flag is set
	 * to true, then all descendants will be offset as well
	 * 
	 * @param node
	 * @param offset
	 * @param recursive
	 */
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

	/**
	 * Convert the specified node to a lisp-like syntax to expose the tree structure of the node and its descendants in
	 * a form that is easy test during unit testing
	 * 
	 * @param node
	 * @return
	 */
	public static String toTreeString(IParseNode node)
	{
		List<String> parts = new ArrayList<String>();

		toTreeString(parts, node);

		return StringUtil.concat(parts);
	}

	private static void toTreeString(List<String> buffer, IParseNode node)
	{
		// TODO: Move to an iterative (non-recursive) implementation if this gets used outside of unit testing
		buffer.add("("); //$NON-NLS-1$
		buffer.add(node.getElementName());

		if (node.hasChildren())
		{
			IParseNode lastChild = node.getLastChild();

			buffer.add(" "); //$NON-NLS-1$

			for (IParseNode child : node)
			{
				toTreeString(buffer, child);

				if (child != lastChild)
				{
					buffer.add(" "); //$NON-NLS-1$
				}
			}
		}

		buffer.add(")"); //$NON-NLS-1$
	}
}
