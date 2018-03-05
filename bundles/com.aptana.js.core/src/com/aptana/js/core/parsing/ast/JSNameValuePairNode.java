/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

import beaver.Symbol;

public class JSNameValuePairNode extends JSNode
{
	private Symbol _colon;

	/**
	 * Used by ANTLR AST for typical property: value definitions
	 * @param colon
	 */
	public JSNameValuePairNode(int start, int end, Symbol colon)
	{
		super(IJSNodeTypes.NAME_VALUE_PAIR);
		this._colon = colon;
		this.setLocation(start, end);
	}

	/**
	 * Used by ANTLR AST for method definitions in classes/objects.
	 */
	public JSNameValuePairNode(int start, int end)
	{
		this(start, end, null);
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

	public boolean isSetter()
	{
		return false;
	}

	public boolean isGetter()
	{
		return false;
	}

	/**
	 * getColon
	 * 
	 * @return
	 */
	public Symbol getColon()
	{
		return this._colon;
	}

	/**
	 * getName
	 */
	public IParseNode getName()
	{
		return this.getChild(0);
	}

	/**
	 * getValue
	 * 
	 * @return
	 */
	public IParseNode getValue()
	{
		return this.getChild(getChildCount() - 1);
	}

	public JSParametersNode getParameters()
	{
		if (getChildCount() == 3)
		{
			return (JSParametersNode) this.getChild(1);
		}
		return null;
	}

	public void setStatic()
	{
		IParseNode node = getLastChild();
		if (node instanceof JSFunctionNode)
		{
			JSFunctionNode func = (JSFunctionNode) node;
			func.setStatic();
		}
	}
}
