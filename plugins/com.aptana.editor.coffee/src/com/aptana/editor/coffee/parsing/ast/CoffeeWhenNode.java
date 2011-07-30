/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import java.util.List;

public class CoffeeWhenNode extends CoffeeNode
{

	public CoffeeWhenNode(List<CoffeeNode> simpleArgs, CoffeeNode block)
	{
		super(CoffeeNodeTypes.WHEN);
		for (CoffeeNode simpleArg : simpleArgs)
		{
			addChild(simpleArg);
		}
		addChild(block);
	}

	@Override
	public String getText()
	{
		return "When"; //$NON-NLS-1$
	}
}
