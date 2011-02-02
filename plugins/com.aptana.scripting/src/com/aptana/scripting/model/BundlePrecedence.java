/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.util.Comparator;

public enum BundlePrecedence implements Comparator<BundlePrecedence>
{
	UNKNOWN(0),
	APPLICATION(1),
	USER(2),
	PROJECT(3);
	
	private int _index;
	
	/**
	 * BundleScope
	 * 
	 * @param value
	 */
	private BundlePrecedence(int value)
	{
		this._index = value;
	}

	/**
	 * compare
	 */
	public int compare(BundlePrecedence o1, BundlePrecedence o2)
	{
		return o1._index - o2._index;
 	}
	
	/**
	 * getIndex
	 * 
	 * @return
	 */
	public int getIndex()
	{
		return this._index;
	}
}
