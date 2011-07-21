/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

public class CoffeeCatchNode extends CoffeeNode
{

	public CoffeeCatchNode(CoffeeLiteralNode identifier, CoffeeBlockNode block)
	{
		super(CoffeeNodeTypes.CATCH);
		addChild(identifier);
		addChild(block);
	}

	public CoffeeLiteralNode getIdentifier()
	{
		return (CoffeeLiteralNode) getChild(0);
	}

	public CoffeeBlockNode getBlock()
	{
		return (CoffeeBlockNode) getChild(1);
	}

	@Override
	public String getText()
	{
		return "Catch"; //$NON-NLS-1$
	}

}
