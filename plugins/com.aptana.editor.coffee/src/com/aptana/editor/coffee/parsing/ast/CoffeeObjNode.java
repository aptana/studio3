/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import java.util.List;

public class CoffeeObjNode extends CoffeeNode
{

	@SuppressWarnings("unused")
	private String optComma;

	public CoffeeObjNode(String optComma, List<CoffeeNode> assignList)
	{
		super(CoffeeNodeTypes.OBJECT);
		this.optComma = optComma;
		for (CoffeeNode assign : assignList)
		{
			addChild(assign);
		}
	}

	@Override
	public String getText()
	{
		return "Obj"; //$NON-NLS-1$
	}
}
