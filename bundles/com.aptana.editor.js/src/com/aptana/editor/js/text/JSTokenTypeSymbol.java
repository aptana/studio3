/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import beaver.Symbol;

import com.aptana.js.core.parsing.JSTokenType;

public class JSTokenTypeSymbol extends Symbol
{

	public final JSTokenType token;

	public JSTokenTypeSymbol(JSTokenType id, int left, int right, Object value)
	{
		super(id.getIndex(), left, right, value);
		this.token = id;
	}

}
