/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class CoffeeForSourceNode extends CoffeeNode
{
	public boolean own;
	public IParseNode name;
	public IParseNode index;
	CoffeeNode source;
	CoffeeNode guard;
	CoffeeNode step;
	boolean object;

	public CoffeeForSourceNode(CoffeeNode source)
	{
		this(source, null, null, false);
	}

	public CoffeeForSourceNode(CoffeeNode source, boolean object)
	{
		this(source, null, null, object);
	}

	public CoffeeForSourceNode(CoffeeNode source, CoffeeNode guard)
	{
		this(source, guard, null, false);
	}

	public CoffeeForSourceNode(CoffeeNode source, CoffeeNode guard, boolean object)
	{
		this(source, guard, null, object);
	}

	public CoffeeForSourceNode(CoffeeNode source, CoffeeNode guard, CoffeeNode step)
	{
		this(source, guard, step, false);
	}

	public CoffeeForSourceNode(CoffeeNode source, CoffeeNode guard, CoffeeNode step, boolean object)
	{
		super(CoffeeNodeTypes.FORIN);
		this.source = source;
		this.guard = guard;
		this.step = step;
		this.object = object;
	}

}
