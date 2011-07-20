/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import java.util.List;

import com.aptana.parsing.ast.IParseNode;

public class CoffeeSwitchNode extends CoffeeNode
{

	public CoffeeSwitchNode(CoffeeNode exp, List<CoffeeWhenNode> whens)
	{
		this(exp, whens, null);
	}

	public CoffeeSwitchNode(CoffeeNode exp, List<CoffeeWhenNode> whens, CoffeeNode block)
	{
		super(CoffeeNodeTypes.SWITCH);
		addChild(exp);
		// flatten When
		for (CoffeeWhenNode when : whens)
		{
			for (IParseNode child : when.getChildren())
			{
				addChild(child);
			}
		}
		if (block != null)
		{
			addChild(block);
		}
	}

	@Override
	public String getText()
	{
		return "Switch"; //$NON-NLS-1$
	}
}
