/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import java.text.MessageFormat;

public class CoffeeAccessNode extends CoffeeNode
{

	private String string;

	public CoffeeAccessNode(CoffeeLiteralNode literal, String string)
	{
		super(CoffeeNodeTypes.ACCESS);
		addChild(literal);
		this.string = string;
	}

	public CoffeeAccessNode(CoffeeLiteralNode literal)
	{
		this(literal, null);
	}

	CoffeeLiteralNode name()
	{
		return (CoffeeLiteralNode) getChild(0);
	}

	@Override
	public String getText()
	{
		StringBuilder builder = new StringBuilder("Access"); //$NON-NLS-1$
		if ("soak".equals(string)) //$NON-NLS-1$
		{
			builder.append('?');
		}
		builder.append(MessageFormat.format(" \"{0}\"", getChild(0).getText())); //$NON-NLS-1$
		return builder.toString();
	}

}
