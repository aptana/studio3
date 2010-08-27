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
