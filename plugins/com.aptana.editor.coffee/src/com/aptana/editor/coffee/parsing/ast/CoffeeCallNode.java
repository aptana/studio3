/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import java.util.List;

public class CoffeeCallNode extends CoffeeNode
{

	@SuppressWarnings("unused")
	private String methodName;
	// Do we check for existence? ('?' trails call)
	private boolean optFuncExists;
	public boolean isDo;

	public CoffeeCallNode(String methodname, List<? extends CoffeeNode> args)
	{
		super(CoffeeNodeTypes.CALL);
		this.methodName = methodname;
		for (CoffeeNode arg : args)
		{
			addChild(arg);
		}
	}

	public CoffeeCallNode(CoffeeNode valueOrInvocation, List<? extends CoffeeNode> args, Boolean optFuncExists)
	{
		super(CoffeeNodeTypes.CALL);
		addChild(valueOrInvocation);
		for (CoffeeNode arg : args)
		{
			addChild(arg);
		}
		this.optFuncExists = optFuncExists;
	}

	@Override
	public String getText()
	{
		if (optFuncExists)
		{
			return "Call?"; //$NON-NLS-1$
		}
		return "Call"; //$NON-NLS-1$
	}

	CoffeeCallNode newInstance()
	{
		// Object base = this.variable.base || this.variable;
		// if (base instanceof CoffeeCallNode)
		// {
		// base.newInstance();
		// }
		// else
		// {
		// this.isNew = true;
		// }
		return this;
	}

}
