package com.aptana.scripting.model;

import java.util.Comparator;

public enum BundleScope implements Comparator<BundleScope>
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
	private BundleScope(int value)
	{
		if (value < 0)
		{
			throw new IllegalArgumentException("BundleScope enumeration value must be greater than or equal to zero");
		}
		
		this._index = value;
	}

	/**
	 * compare
	 */
	public int compare(BundleScope o1, BundleScope o2)
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
