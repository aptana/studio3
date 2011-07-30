/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

public class CoffeeClassNode extends CoffeeNode
{

	private CoffeeValueNode variable;

	public CoffeeClassNode()
	{
		this(null, null, null);
	}

	public CoffeeClassNode(CoffeeValueNode simpleAssignable)
	{
		this(simpleAssignable, null, null);
	}

	public CoffeeClassNode(CoffeeValueNode simpleAssignable, CoffeeValueNode superClass)
	{
		this(simpleAssignable, superClass, null);
	}

	public CoffeeClassNode(CoffeeValueNode simpleAssignable, CoffeeValueNode superClass, CoffeeBlockNode block)
	{
		super(CoffeeNodeTypes.CLASS);
		this.variable = simpleAssignable;
		if (simpleAssignable != null)
		{
			addChild(simpleAssignable);
		}
		if (superClass != null)
		{
			addChild(superClass);
		}
		if (block == null)
		{
			block = new CoffeeBlockNode();
		}
		addChild(block);
	}

	@Override
	public String getText()
	{
		return "Class"; //$NON-NLS-1$
	}

	public String determineName()
	{
		if (this.variable == null)
		{
			return null;
		}

		CoffeeNode tail = null;
		if (this.variable.hasProperties())
		{
			tail = (CoffeeNode) this.variable.getChild(this.variable.getChildCount() - 1);
		}
		if (tail != null)
		{
			if (tail instanceof CoffeeAccessNode)
			{
				CoffeeAccessNode blah = (CoffeeAccessNode) tail;
				CoffeeLiteralNode literal = blah.name();
				return literal.getText();
			}
		}
		else
		{
			CoffeeNode base = this.variable.base();
			return base.getText();
		}
		return null;
	}

	public CoffeeBlockNode getBlock()
	{
		return (CoffeeBlockNode) getChild(getChildCount() - 1);
	}

}
