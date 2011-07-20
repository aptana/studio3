/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.parsing.ast.IParseNode;

public class CoffeeCodeNode extends CoffeeNode
{

	public boolean bound;
	@SuppressWarnings("unused")
	private String funcGlyph;

	public CoffeeCodeNode(CoffeeParamNode[] params, CoffeeNode block, String funcGlyph)
	{
		super(CoffeeNodeTypes.CODE);
		for (CoffeeParamNode param : params)
		{
			addChild(param);
		}
		if (block == null)
		{
			block = new CoffeeBlockNode();
		}
		addChild(block);
		this.funcGlyph = funcGlyph;
		this.bound = ("boundfunc".equals(funcGlyph)); //$NON-NLS-1$
	}

	public CoffeeCodeNode(CoffeeBlockNode b, String f)
	{
		this(new CoffeeParamNode[0], b, f);
	}

	@Override
	public String getText()
	{
		return "Code"; //$NON-NLS-1$
	}

	public CoffeeNode getBlock()
	{
		return (CoffeeNode) getChild(getChildCount() - 1);
	}

	List<CoffeeParamNode> params()
	{
		if (getChildCount() > 1)
		{
			List<CoffeeParamNode> children = new ArrayList<CoffeeParamNode>(getChildCount());
			for (IParseNode child : getChildren())
			{
				if (child instanceof CoffeeParamNode)
				{
					children.add((CoffeeParamNode) child);
				}
			}
			return children;
		}
		return Collections.emptyList();
	}
}
