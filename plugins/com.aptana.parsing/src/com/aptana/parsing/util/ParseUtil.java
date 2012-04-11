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

import com.aptana.core.IFilter;
import com.aptana.core.util.StringUtil;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseRootNode;

/**
 * ParseUtil
 */
public class ParseUtil
{
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
	public static void addOffset(IParseNode node, final int offset, boolean recursive)
	{
		IFilter<IParseNode> function = new IFilter<IParseNode>()
		{
			public boolean include(IParseNode item)
			{
				if (item instanceof ParseNode)
				{
					((ParseNode) item).addOffset(offset);
				}

				return true;
			}
		};

		treeApply(node, function, recursive);
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

	/**
	 * A private helper function for {@link #toTreeString(IParseNode)}
	 * 
	 * @param buffer
	 * @param node
	 */
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

	/**
	 * Visit the specified node and all of its descendants, applying the specified function to each.
	 * 
	 * @param node
	 *            The node to visit
	 * @param function
	 *            A function to apply to each visited node
	 */
	public static void treeApply(IParseNode node, IFilter<IParseNode> function)
	{
		treeApply(node, function, true);
	}

	/**
	 * Visit the specified node and apply a function to it. If the recursive flag is set, then the function will be
	 * applied to all of its descendants as well. This implementation uses a {@link Queue} internally to prevent
	 * potential stack overflow associated with recursive tree walking.
	 * 
	 * @param node
	 *            The node to visit
	 * @param function
	 *            A function to apply to each visited node
	 * @param recursive
	 *            A flag indicating if all descendants of the node should be visited or not
	 */
	public static void treeApply(IParseNode node, IFilter<IParseNode> function, boolean recursive)
	{
		if (node == null || function == null)
		{
			return;
		}

		Queue<IParseNode> queue = new LinkedList<IParseNode>();

		// prime queue
		queue.offer(node);

		while (!queue.isEmpty())
		{
			IParseNode current = queue.poll();

			// TODO: wrap function call in try/block?

			// apply function
			if (!function.include(current))
			{
				// stop processing if the function indicates to do so
				break;
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
	 * Trim memory usage for the specified node (and its descendants).
	 * 
	 * @param node
	 *            The node to trim
	 */
	public static void trimToSize(IParseNode node)
	{
		trimToSize(node, true);
	}

	/**
	 * Trim memory usage for the specified node. If the recursive flag is set to true, then all descendants will be
	 * trimmed as well
	 * 
	 * @param node
	 *            The node to trim
	 * @param recursive
	 *            A flag indicating if descendant nodes should be trimmed as well
	 */
	public static void trimToSize(IParseNode node, boolean recursive)
	{
		IFilter<IParseNode> function = new IFilter<IParseNode>()
		{
			public boolean include(IParseNode item)
			{
				if (item instanceof ParseNode)
				{
					((ParseNode) item).trimToSize();
				}

				return true;
			}
		};

		treeApply(node, function, recursive);
	}

	private ParseUtil()
	{
	}
}
