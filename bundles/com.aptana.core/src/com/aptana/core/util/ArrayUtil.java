/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrayUtil
{

	/**
	 * Immutable arrays that can be re-used across system to avoid creating lots of empty array copies.
	 */
	public static final String[] NO_STRINGS = new String[0];
	public static final Object[] NO_OBJECTS = new Object[0];
	public static final byte[] NO_BYTES = new byte[0];

	private ArrayUtil()
	{
	}

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
		{
			throw new IllegalArgumentException("Must have at least one non-null array value"); //$NON-NLS-1$
		}

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

	@SuppressWarnings("unchecked")
	public static <T> T[] reverse(T[] array)
	{
		List<T> list = Arrays.asList(array);
		Collections.reverse(list);
		return (T[]) list.toArray();
	}

	public static boolean isEmpty(Object[] array)
	{
		return array == null || array.length == 0;
	}

	/**
	 * Returns <code>true</code> in case all the elements in the given array are <code>null</code>.
	 * 
	 * @param array
	 * @return <code>true</code> in case all the array's elements are <code>null</code>. Also, returns <code>true</code>
	 *         in case the given array itself is <code>null</code>.
	 */
	public static boolean isAllNulls(Object[] array)
	{
		if (array == null)
		{
			return true;
		}
		for (Object obj : array)
		{
			if (obj != null)
			{
				return false;
			}
		}
		return true;
	}

	public static int length(Object[] array)
	{
		if (isEmpty(array))
		{
			return 0;
		}
		return array.length;
	}

	public static boolean contains(int[] array, int item)
	{
		if (array == null || array.length == 0)
		{
			return false;
		}
		for (int x : array)
		{
			if (x == item)
			{
				return true;
			}
		}
		return false;
	}

	public static Boolean contains(String[] array, String string)
	{
		if (array == null || array.length == 0)
		{
			return false;
		}
		for (String x : array)
		{
			if (x.equals(string))
			{
				return true;
			}
		}
		return false;
	}
}
