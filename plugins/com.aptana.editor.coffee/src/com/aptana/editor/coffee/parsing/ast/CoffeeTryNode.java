/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

public class CoffeeTryNode extends CoffeeNode
{

	public CoffeeTryNode(CoffeeNode block)
	{
		this(block, null, null, null);
	}

	public CoffeeTryNode(CoffeeNode tryBlock, CoffeeLiteralNode catchIdentifier, CoffeeNode catchBlock)
	{
		this(tryBlock, catchIdentifier, catchBlock, null);
	}

	public CoffeeTryNode(CoffeeNode tryBlock, CoffeeLiteralNode catchIdentifier, CoffeeNode catchBlock,
			CoffeeNode finallyBlock)
	{
		super(CoffeeNodeTypes.TRY);
		if (tryBlock != null)
		{
			addChild(tryBlock);
		}
		if (catchIdentifier != null)
		{
			addChild(catchIdentifier);
		}
		if (catchBlock != null)
		{
			addChild(catchBlock);
		}
		if (finallyBlock != null)
		{
			addChild(finallyBlock);
		}
	}

	@Override
	public String getText()
	{
		return "Try";
	}

}
