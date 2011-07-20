/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import java.util.List;

public class CoffeeArrNode extends CoffeeNode
{

	public CoffeeArrNode(List<CoffeeNode> args)
	{
		super(CoffeeNodeTypes.ARRAY);
		for (CoffeeNode arg : args)
		{
			addChild(arg);
		}
	}

	@Override
	public String getText()
	{
		return "Arr"; //$NON-NLS-1$
	}

}
