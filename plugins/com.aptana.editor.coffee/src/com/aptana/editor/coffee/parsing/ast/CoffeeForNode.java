/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

@SuppressWarnings("unused")
public class CoffeeForNode extends CoffeeNode
{

	private CoffeeNode source;
	private CoffeeNode guard;
	private CoffeeNode step;
	private IParseNode name;
	private IParseNode index;
	private boolean own;
	private boolean object;

	public CoffeeForNode(CoffeeNode body, CoffeeForSourceNode source)
	{
		super(CoffeeNodeTypes.FOR);

		this.source = source.source;
		this.guard = source.guard;
		this.step = source.step;
		this.name = source.name;
		this.index = source.index;
		this.own = source.own;
		this.object = source.object;

		if (this.object)
		{
			this.name = source.index;
			this.index = source.name;
		}

		// Wrap the statement in a block
		addChild(CoffeeBlockNode.wrap(body));
		addChild(this.source);
	}

	@Override
	public String getText()
	{
		return "For"; //$NON-NLS-1$
	}
}
