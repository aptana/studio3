/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

public class CoffeeRangeNode extends CoffeeNode
{

	@SuppressWarnings("unused")
	private String rangeDots;

	public CoffeeRangeNode(CoffeeNode leftExpr, CoffeeNode rightExpr, String rangeDots)
	{
		super(CoffeeNodeTypes.RANGE);
		if (leftExpr != null)
		{
			addChild(leftExpr);
		}
		if (rightExpr != null)
		{
			addChild(rightExpr);
		}
		this.rangeDots = rangeDots;
	}

	@Override
	public String getText()
	{
		return "Range"; //$NON-NLS-1$
	}

}
