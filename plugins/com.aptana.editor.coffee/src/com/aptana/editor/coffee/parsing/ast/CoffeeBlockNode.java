/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class CoffeeBlockNode extends CoffeeNode
{

	public CoffeeBlockNode(CoffeeNode line)
	{
		this();
		addChild(line);
	}

	public CoffeeBlockNode()
	{
		super(CoffeeNodeTypes.BLOCK);
	}

	@Override
	public String getText()
	{
		return "Block"; //$NON-NLS-1$
	}

	public static IParseNode wrap(CoffeeNode body)
	{
		if (body instanceof CoffeeBlockNode)
		{
			return body;
		}
		return new CoffeeBlockNode(body);
	}

	protected CoffeeNode unwrap()
	{
		if (getChildCount() == 1)
		{
			return (CoffeeNode) getChild(0);
		}
		return this;
	}
}
