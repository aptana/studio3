/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

public class CoffeeSliceNode extends CoffeeIndexNode
{

	public CoffeeSliceNode(CoffeeRangeNode range)
	{
		super(range);
		this.fType = CoffeeNodeTypes.SLICE;
	}

	@Override
	public String getText()
	{
		return "Slice";	//$NON-NLS-1$
	}
}
