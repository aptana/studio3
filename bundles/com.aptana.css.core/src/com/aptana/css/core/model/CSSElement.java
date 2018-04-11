/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.model;

import com.aptana.index.core.Index;

/**
 * CSSElement
 */
public class CSSElement extends BaseElement
{

	private Index index;

	public CSSElement(Index index)
	{
		this.index = index;
		setName(Messages.CSSElement_ElementName);
	}

	public Index getIndex()
	{
		return index;
	}
}
