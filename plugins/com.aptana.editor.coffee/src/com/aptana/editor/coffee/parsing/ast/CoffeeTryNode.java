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

	public CoffeeTryNode(CoffeeBlockNode block)
	{
		this(block, null, null, null);
	}

	public CoffeeTryNode(CoffeeBlockNode tryBlock, CoffeeLiteralNode catchIdentifier, CoffeeBlockNode catchBlock)
	{
		this(tryBlock, catchIdentifier, catchBlock, null);
	}

	public CoffeeTryNode(CoffeeBlockNode tryBlock, CoffeeLiteralNode catchIdentifier, CoffeeBlockNode catchBlock,
			CoffeeBlockNode finallyBlock)
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

	public CoffeeNode getTryBlock()
	{
		return (CoffeeNode) getChild(0);
	}

	public CoffeeLiteralNode getCatchIdentifier()
	{
		if (getChildCount() >= 2)
		{
			return (CoffeeLiteralNode) getChild(1);
		}
		return null;
	}

	public CoffeeNode getCatchBlock()
	{
		if (getChildCount() >= 3)
		{
			return (CoffeeNode) getChild(2);
		}
		return null;
	}

	public CoffeeNode getFinallyBlock()
	{
		if (getChildCount() == 4)
		{
			return (CoffeeNode) getChild(3);
		}
		return null;
	}

	@Override
	public String getText()
	{
		return "Try"; //$NON-NLS-1$
	}

}
