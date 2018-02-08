/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json.core.parsing.ast;

import beaver.Symbol;

import com.aptana.json.core.IJSONConstants;
import com.aptana.parsing.ast.ParseRootNode;

/**
 * @author klindsey
 */
public class JSONParseRootNode extends ParseRootNode
{

	/**
	 * JSParseRootNode
	 */
	public JSONParseRootNode()
	{
		this(null);
	}

	/**
	 * JSParseRootNode
	 * 
	 * @param children
	 * @param start
	 * @param end
	 */
	public JSONParseRootNode(Symbol[] children)
	{
		super(children);
	}

	public String getLanguage()
	{
		return IJSONConstants.CONTENT_TYPE_JSON;
	}

	/**
	 * accept
	 * 
	 * @param walker
	 */
	public void accept(JSONTreeWalker walker)
	{
		walker.visit(this);
	}
}
