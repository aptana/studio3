/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

public class CoffeeExistenceNode extends CoffeeNode
{

	public CoffeeExistenceNode(CoffeeNode expression)
	{
		super(CoffeeNodeTypes.EXISTENCE);
		addChild(expression);
	}

	@Override
	public String getText()
	{
		return "Existence"; //$NON-NLS-1$
	}

	@Override
	public CoffeeNode invert()
	{
		this.negated = !this.negated;
		return this;
	}

}
