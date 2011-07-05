/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

public class CoffeeLiteralNode extends CoffeeNode
{
	public boolean isUndefined;
	private String literal;

	public CoffeeLiteralNode(String literal)
	{
		super(CoffeeNodeTypes.LITERAL);
		this.literal = literal;
	}

	@Override
	public String getText()
	{
		return this.literal;
	}

}
