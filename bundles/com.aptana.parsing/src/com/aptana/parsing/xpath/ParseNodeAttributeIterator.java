/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.xpath;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.aptana.parsing.ast.IParseNodeAttribute;

/**
 * ParseNodeAttributeIterator
 */
public class ParseNodeAttributeIterator implements Iterator<Object>
{
	private int _index;
	private IParseNodeAttribute[] _attributes;

	/**
	 * @param attributes
	 */
	public ParseNodeAttributeIterator(IParseNodeAttribute[] attributes)
	{
		this._attributes = attributes;
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext()
	{
		return this._index < this._attributes.length;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next()
	{
		if (this.hasNext())
		{
			return this._attributes[this._index++];
		}
		else
		{
			throw new NoSuchElementException(); // $codepro.audit.disable exceptionUsage.exceptionCreation
		}
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove()
	{
		throw new UnsupportedOperationException(); // $codepro.audit.disable exceptionUsage.exceptionCreation
	}
}
