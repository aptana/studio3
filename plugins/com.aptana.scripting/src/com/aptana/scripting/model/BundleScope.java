package com.aptana.scripting.model;

import java.util.Comparator;

public enum BundleScope implements Comparator<BundleScope>
{
	APPLICATION(0),
	USER(1),
	PROJECT(2);
	
	private static int maxValue = 0;
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
		
		setMaxValue(value);
	}
	
	/**
	 * getMaxValue
	 * 
	 * @return
	 */
	public static int getMaxValue()
	{
		return maxValue;
	}
	
	/**
	 * setMaxValue
	 * 
	 * @param value
	 */
	private static void setMaxValue(int value)
	{
		if (value > maxValue)
		{
			maxValue = value;
		}
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
