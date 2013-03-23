/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.model;

import com.aptana.index.core.Index;

/**
 * JSElement
 */
public class JSElement extends BaseElement
{

	private Index index;

	/**
	 * An element used to group JS content in an Index
	 * 
	 * @param index
	 *            The index that contains JS content
	 */
	public JSElement(Index index)
	{
		this.index = index;
		setName(Messages.JSElement_NodeLabel);
	}

	/**
	 * Returns the element associated with this element
	 * 
	 * @return Returns an Index
	 */
	public Index getIndex()
	{
		return index;
	}
}
