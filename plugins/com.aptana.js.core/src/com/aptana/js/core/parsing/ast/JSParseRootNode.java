/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.inferencing.JSScope;
import com.aptana.js.internal.core.inferencing.JSSymbolCollector;
import com.aptana.parsing.ast.ParseRootNode;

public class JSParseRootNode extends ParseRootNode
{
	/**
	 * JSParseRootNode
	 */
	public JSParseRootNode()
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
	public JSParseRootNode(Symbol[] children)
	{
		super(children);
	}

	public String getLanguage()
	{
		return IJSConstants.CONTENT_TYPE_JS;
	}

	/**
	 * accept
	 * 
	 * @param walker
	 */
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}

	/**
	 * getGlobals
	 * 
	 * @return
	 */
	public JSScope getGlobals()
	{
		JSSymbolCollector s = new JSSymbolCollector();

		this.accept(s);

		return s.getScope();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	public String toString()
	{
		JSFormatWalker walker = new JSFormatWalker();

		this.accept(walker);

		return walker.getText();
	}
}
