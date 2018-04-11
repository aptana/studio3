/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

import beaver.Symbol;

import com.aptana.css.core.ICSSConstants;
import com.aptana.parsing.ast.ParseRootNode;

/**
 * CSSParseRootNode
 */
public class CSSParseRootNode extends ParseRootNode
{

	/**
	 * CSSParseRootNode
	 */
	public CSSParseRootNode()
	{
		this(null);
	}

	/**
	 * CSSParseRootNode
	 * 
	 * @param children
	 */
	public CSSParseRootNode(Symbol[] children)
	{
		super(children);
	}

	public String getLanguage()
	{
		return ICSSConstants.CONTENT_TYPE_CSS;
	}

	public void accept(CSSTreeWalker walker)
	{
		walker.visit(this);
	}
}
