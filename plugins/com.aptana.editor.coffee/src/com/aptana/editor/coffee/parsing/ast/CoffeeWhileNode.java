/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

public class CoffeeWhileNode extends CoffeeNode
{

	public CoffeeWhileNode(CoffeeNode condition)
	{
		this(condition, null);
	}

	public CoffeeWhileNode(CoffeeNode condition, CoffeeNode guard)
	{
		this(condition, guard, false);
	}

	public CoffeeWhileNode(CoffeeNode condition, boolean invert)
	{
		this(condition, null, invert);
	}

	public CoffeeWhileNode(CoffeeNode condition, CoffeeNode guard, boolean invert)
	{
		super(CoffeeNodeTypes.WHILE);
		if (invert)
		{
			condition = condition.invert();
		}
		addChild(condition);
		if (guard != null)
		{
			addChild(guard);
		}
	}

	public CoffeeWhileNode addBody(CoffeeNode body)
	{
		addChild(body);
		return this;
	}

	@Override
	public String getText()
	{
		return "While"; //$NON-NLS-1$
	}
}
