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

import com.aptana.parsing.ast.IParseNode;

/**
 * @author Kevin Lindsey
 */
public abstract class ParseNodeIterator implements Iterator<Object>
{
	private IParseNode _node;

	/**
	 * ParseNodeIterator
	 * 
	 * @param node
	 */
	protected ParseNodeIterator(IParseNode node)
	{
		this._node = this.getFirstNode(node);
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove()
	{
		throw new UnsupportedOperationException(); // $codepro.audit.disable exceptionUsage.exceptionCreation
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext()
	{
		return this._node != null;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next()
	{
		IParseNode result = _node;

		if (this._node == null)
		{
			throw new NoSuchElementException(); // $codepro.audit.disable exceptionUsage.exceptionCreation
		}
		else
		{
			this._node = this.getNextNode(this._node);
		}

		// System.out.println("next = " + result.getName() + ", " + ((IParseNode) result).getSource());

		return result;
	}

	/**
	 * getFirstNode
	 * 
	 * @param node
	 * @return IParseNode
	 */
	protected abstract IParseNode getFirstNode(IParseNode node);

	/**
	 * getNextNode
	 * 
	 * @param node
	 * @return IParseNode
	 */
	protected abstract IParseNode getNextNode(IParseNode node);
}
