/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.coffee.ICoffeeConstants;
import com.aptana.parsing.ast.ParseRootNode;

/**
 * CoffeeParseRootNode
 */
public class CoffeeParseRootNode extends ParseRootNode
{
	/**
	 * CoffeeParseRootNode
	 */
	public CoffeeParseRootNode()
	{
		this(new Symbol[0]);
	}

	/**
	 * CoffeeParseRootNode
	 * 
	 * @param children
	 */
	public CoffeeParseRootNode(Symbol... children)
	{
		super( //
				ICoffeeConstants.CONTENT_TYPE_COFFEE, //
				(children != null) ? children : new Symbol[0], //
				(children != null && children.length > 0) ? children[0].getStart() : 0, //
				(children != null && children.length > 0) ? children[children.length - 1].getEnd() : 0);
	}
}
