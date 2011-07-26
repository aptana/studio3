/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

public class ArrayUtil
{

	/**
	 * Flattens the multiple separate arrays into a single array
	 * 
	 * @param arr
	 *            Multiple arrays
	 * @return An array of the individual arrays flattened together
	 */
	public static <T> T[] flatten(T[]... arrays)
	{
		if (arrays == null)
			throw new IllegalArgumentException("Must have at least one non-null array value");

		int arraySize = 0;
		for (Object[] arr : arrays)
		{
			arraySize += arr.length;
		}

		@SuppressWarnings("unchecked")
		final T[] returnArray = (T[]) java.lang.reflect.Array.newInstance(arrays[0].getClass().getComponentType(),
				arraySize);
		int start = 0;
		for (Object[] arr : arrays)
		{
			System.arraycopy(arr, 0, returnArray, start, arr.length);
			start += arr.length;
		}
		return returnArray;
	}
}
