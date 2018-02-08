/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json.core.parsing.ast;

import beaver.Symbol;

/**
 * JSONEntryNode
 */
public class JSONEntryNode extends JSONNode
{
	private Symbol _colon;

	/**
	 * JSONEntryNode
	 */
	public JSONEntryNode(Symbol colon)
	{
		super(JSONNodeType.ENTRY);

		this._colon = colon;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.core.parsing.ast.JSONNode#accept(com.aptana.json.core.parsing.ast.JSONTreeWalker)
	 */
	@Override
	public void accept(JSONTreeWalker walker)
	{
		walker.visit(this);
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
}
