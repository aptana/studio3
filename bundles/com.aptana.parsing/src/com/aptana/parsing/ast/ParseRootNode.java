/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.ast;

import java.util.ArrayList;
import java.util.List;

import beaver.Symbol;

public abstract class ParseRootNode extends ParseNode implements IParseRootNode
{
	private IParseNode[] fComments;

	/**
	 * Constructor to be used if the start will be the start of the first node and the end the end of the last node.
	 * 
	 * @param children
	 *            may be null (in which case it's considered as having no children with start = end = 0.
	 */
	protected ParseRootNode(Symbol[] children)
	{
		super();
		int start = 0;
		int end = 0;
		final IParseNode[] parseNodes;
		if (children == null || children.length == 0)
		{
			parseNodes = NO_CHILDREN;
		}
		else
		{
			List<IParseNode> nodes = filterParseNodesList(children);
			int nodesLen = nodes.size();
			if (nodesLen == 0)
			{
				parseNodes = NO_CHILDREN;
			}
			else
			{
				parseNodes = nodes.toArray(new IParseNode[nodesLen]);
				start = ((Symbol) parseNodes[0]).getStart();
				end = ((Symbol) parseNodes[nodesLen - 1]).getEnd();
			}
		}
		initialize(parseNodes, start, end);
	}

	public ParseRootNode(Symbol[] children, int start, int end)
	{
		super();
		List<IParseNode> nodes = filterParseNodesList(children);
		initialize(nodes.toArray(new IParseNode[nodes.size()]), start, end);
	}

	/**
	 * @return only the symbols that are parse nodes (i.e.: remove null / others).
	 */
	private List<IParseNode> filterParseNodesList(Symbol[] children)
	{
		int len = children.length;

		List<IParseNode> nodes = new ArrayList<IParseNode>(len);
		// Micro-optimization (using old for to save on speed/memory).
		for (int i = 0; i < len; i++)
		{
			Symbol child = children[i];
			if (child instanceof IParseNode)
			{
				nodes.add((IParseNode) child);
			}
		}
		return nodes;
	}

	private void initialize(IParseNode[] children, int start, int end)
	{
		this.start = start;
		this.end = end;

		setChildren(children);

		fComments = NO_CHILDREN;
	}

	public IParseNode[] getCommentNodes()
	{
		return fComments;
	}

	public void setCommentNodes(IParseNode[] comments)
	{
		fComments = comments;
	}
}
