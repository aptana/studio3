/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

/**
 * JSONTreeWalker
 */
public class JSONTreeWalker
{
	public void visit(JSONArrayNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSONEntryNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSONFalseNode node)
	{
		// leaf
	}

	public void visit(JSONNullNode node)
	{
		// leaf
	}

	public void visit(JSONNumberNode node)
	{
		// leaf
	}

	public void visit(JSONObjectNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSONParseRootNode node)
	{
		for (IParseNode child : node)
		{
			if (child instanceof JSONNode)
			{
				((JSONNode) child).accept(this);
			}
		}
	}

	public void visit(JSONStringNode node)
	{
		// leaf
	}

	public void visit(JSONTrueNode node)
	{
		// leaf
	}

	/**
	 * visitChildren
	 * 
	 * @param node
	 */
	protected void visitChildren(JSONNode node)
	{
		for (IParseNode child : node)
		{
			if (child instanceof JSONNode)
			{
				((JSONNode) child).accept(this);
			}
		}
	}
}
