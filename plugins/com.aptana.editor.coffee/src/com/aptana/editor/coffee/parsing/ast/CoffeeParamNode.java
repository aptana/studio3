/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import java.text.MessageFormat;

import com.aptana.parsing.ast.IParseNode;

public class CoffeeParamNode extends CoffeeNode
{

	@SuppressWarnings("unused")
	private boolean ellipsis;

	public CoffeeParamNode(CoffeeNode var)
	{
		this(var, null, false);
	}

	public CoffeeParamNode(CoffeeNode var, CoffeeNode expression)
	{
		this(var, expression, false);
	}

	public CoffeeParamNode(CoffeeNode var, CoffeeNode expression, boolean ellipsis)
	{
		super(CoffeeNodeTypes.PARAM_VAR);
		addChild(var);
		if (expression != null)
		{
			addChild(expression);
		}
		this.ellipsis = ellipsis;
	}

	@Override
	public String getText()
	{
		IParseNode child = getChild(0);
		if (child instanceof CoffeeLiteralNode)
		{
			return MessageFormat.format("Param \"{0}\"", child.getText()); //$NON-NLS-1$
		}
		return "Param"; //$NON-NLS-1$
	}
}
