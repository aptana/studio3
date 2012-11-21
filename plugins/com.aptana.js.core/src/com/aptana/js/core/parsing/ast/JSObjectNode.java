/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

public class JSObjectNode extends JSNode
{
	private Symbol _leftBrace;
	private Symbol _rightBrace;

	/**
	 * JSObjectNode
	 * 
	 * @param leftBrace
	 * @param properties
	 * @param rightBrace
	 */
	public JSObjectNode(Symbol leftBrace, Symbol rightBrace, JSNode... properties)
	{
		super(IJSNodeTypes.OBJECT_LITERAL, properties);

		this._leftBrace = leftBrace;
		this._rightBrace = rightBrace;
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

	/**
	 * getLeftBrace
	 * 
	 * @return
	 */
	public Symbol getLeftBrace()
	{
		return this._leftBrace;
	}

	/**
	 * getRightBrace
	 * 
	 * @return
	 */
	public Symbol getRightBrace()
	{
		return this._rightBrace;
	}
}
