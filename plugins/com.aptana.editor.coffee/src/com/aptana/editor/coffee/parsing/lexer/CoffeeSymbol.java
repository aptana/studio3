/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.lexer;

import java.text.MessageFormat;

import beaver.Symbol;

import com.aptana.editor.coffee.parsing.Terminals;

public class CoffeeSymbol extends Symbol
{

	public boolean spaced;
	public boolean newLine;
	public boolean reserved;
	public boolean generated;
	public boolean fromThen;
	public boolean call;

	public CoffeeSymbol(short id, Object value)
	{
		super(id, value);
	}

	/**
	 * @param id
	 *            type of token
	 * @param start
	 *            start offset
	 * @param end
	 *            end offset
	 * @param value
	 *            string value of source. In/Outdents contain integer value of length
	 */
	public CoffeeSymbol(short id, int start, int end, Object value)
	{
		super(id, start, end, value);
	}

	public Object getValue()
	{
		return value;
	}

	public void setId(short id)
	{
		this.id = id;
	}

	public void setLocation(int start, int end)
	{
		this.start = start;
		this.end = end;
	}

	@Override
	public String toString()
	{
		return coffeeToken();
	}

	/**
	 * Spit out the exact text that coffee -t does so we can easily compare the token list between our lexer and theirs.
	 * 
	 * @return
	 */
	public String coffeeToken()
	{
		Object tmpValue = getValue();
		if ("\n".equals(tmpValue)) //$NON-NLS-1$
		{
			tmpValue = "\\n"; //$NON-NLS-1$
		}
		String name = Terminals.getTokenID(getId());
		if (name == null)
		{
			name = Terminals.getNameForValue(id);
		}
		return MessageFormat.format("[{0} {1}]", name, tmpValue); //$NON-NLS-1$
	}
}
