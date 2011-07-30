/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

public class CoffeeForVariablesNode extends CoffeeNode
{

	public boolean own;

	public CoffeeForVariablesNode(CoffeeNode first)
	{
		this(first, null);
	}

	public CoffeeForVariablesNode(CoffeeNode left, CoffeeNode right)
	{
		super(CoffeeNodeTypes.FOR_VARIABLES);
		if (left != null)
		{
			addChild(left);
		}
		if (right != null)
		{
			addChild(right);
		}
	}
}
