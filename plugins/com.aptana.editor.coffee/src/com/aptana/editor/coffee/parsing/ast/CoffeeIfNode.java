/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

public class CoffeeIfNode extends CoffeeNode
{

	@SuppressWarnings("unused")
	private boolean statement;
	private boolean isChain;
	private CoffeeBlockNode elseBody;

	public CoffeeIfNode(CoffeeNode condition, CoffeeNode body, String type)
	{
		this(condition, body, type, false);
	}

	public CoffeeIfNode(CoffeeNode condition, CoffeeNode body, String type, boolean statement)
	{
		super(CoffeeNodeTypes.IF);
		if ("unless".equals(type)) //$NON-NLS-1$
		{
			condition = condition.invert();
		}
		addChild(condition);

		if (body != null)
		{
			addChild(body);
		}
		this.statement = statement;
		this.isChain = false;
	}

	private CoffeeIfNode elseBodyNode()
	{
		return (CoffeeIfNode) (this.elseBody != null ? this.elseBody.unwrap() : null);
	}

	public CoffeeIfNode addElse(CoffeeNode elseBody)
	{
		if (this.isChain)
		{
			this.elseBodyNode().addElse(elseBody);
		}
		else
		{
			this.isChain = elseBody instanceof CoffeeIfNode;
			this.elseBody = this.ensureBlock(elseBody);
			addChild(this.elseBody);
		}
		return this;
	}

	private CoffeeBlockNode ensureBlock(CoffeeNode node)
	{
		if (node instanceof CoffeeBlockNode)
		{
			return (CoffeeBlockNode) node;
		}
		return new CoffeeBlockNode(node);
	}

	@Override
	public String getText()
	{
		return "If"; //$NON-NLS-1$
	}

}
