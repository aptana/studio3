/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.aptana.parsing.ast.IParseNode;

public class CoffeeValueNode extends CoffeeNode
{

	@SuppressWarnings("unused")
	private String tag;

	public CoffeeValueNode(CoffeeNode objAssignable)
	{
		this(objAssignable, null, null);
	}

	public CoffeeValueNode(CoffeeCallNode invocation, CoffeeNode accessor)
	{
		this(invocation, accessor, null);
	}

	public CoffeeValueNode(CoffeeNode coffeeLiteralNode, CoffeeNode coffeeAccessNode, String tag)
	{
		super(CoffeeNodeTypes.VALUE);

		addChild(coffeeLiteralNode);
		if (coffeeAccessNode != null)
		{
			addChild(coffeeAccessNode);
		}
		this.tag = tag;
	}

	public CoffeeValueNode push(CoffeeNode accessor)
	{
		addChild(accessor);
		return this;
	}

	@Override
	public String getText()
	{
		IParseNode child = getChild(0);
		if (child instanceof CoffeeLiteralNode)
		{
			return MessageFormat.format("Value \"{0}\"", getChild(0).getText()); //$NON-NLS-1$
		}
		return "Value"; //$NON-NLS-1$
	}

	public CoffeeNode unwrap()
	{
		if (hasProperties())
		{
			return this;
		}
		return this.base();
	}

	CoffeeNode base()
	{
		return (CoffeeNode) getChild(0);
	}

	public boolean hasProperties()
	{
		return getChildCount() > 1;
	}

	public boolean isArray()
	{
		return getChildCount() == 1 && this.base() instanceof CoffeeArrNode;
	}

	public boolean isComplex()
	{
		return this.hasProperties() || this.base().isComplex();
	}

	public boolean isAssignable()
	{
		return this.hasProperties() || this.base().isAssignable();
	}

	public List<CoffeeNode> properties()
	{
		List<CoffeeNode> props = new ArrayList<CoffeeNode>();
		for (int i = 1; i < getChildCount(); i++)
		{
			props.add((CoffeeNode) getChild(i));
		}
		return props;
	}

	// public boolean isStatement(o)
	// {
	// return !this.properties.length && this.base.isStatement(o);
	// }
	//
	// public boolean assigns(name)
	// {
	// return !this.properties.length && this.base.assigns(name);
	// }
	//
	// public boolean jumps(o)
	// {
	// return !this.properties.length && this.base.jumps(o);
	// }
}
