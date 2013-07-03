/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
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
		this((Symbol[]) null);
	}

	/**
	 * CoffeeParseRootNode
	 * 
	 * @param children
	 */
	public CoffeeParseRootNode(Symbol... children)
	{
		super(children);
	}

	public String getLanguage()
	{
		return ICoffeeConstants.CONTENT_TYPE_COFFEE;
	}
}
