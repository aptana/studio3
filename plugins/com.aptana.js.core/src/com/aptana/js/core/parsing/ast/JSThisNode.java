/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

import com.aptana.js.core.JSLanguageConstants;

public class JSThisNode extends JSPrimitiveNode
{
	/**
	 * JSThisNode
	 */
	public JSThisNode()
	{
		super(IJSNodeTypes.THIS, JSLanguageConstants.THIS);
	}

	/**
	 * JSThisNode
	 * 
	 * @param identifier
	 */
	public JSThisNode(Symbol identifier)
	{
		this();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#accept(com.aptana.editor.js.parsing.ast.JSTreeWalker)
	 */
	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}
}
