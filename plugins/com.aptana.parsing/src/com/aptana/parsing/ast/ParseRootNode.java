/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.ast;

import java.util.ArrayList;
import java.util.List;

import beaver.Symbol;

public class ParseRootNode extends ParseNode implements IParseRootNode
{
	private IParseNode[] fComments;

	public ParseRootNode(String language, Symbol[] children, int start, int end)
	{
		super(language);
		this.start = start;
		this.end = end;

		List<IParseNode> nodes = new ArrayList<IParseNode>();
		for (Symbol child : children)
		{
			if (child instanceof IParseNode)
			{
				nodes.add((IParseNode) child);
			}
		}
		setChildren(nodes.toArray(new IParseNode[nodes.size()]));

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
