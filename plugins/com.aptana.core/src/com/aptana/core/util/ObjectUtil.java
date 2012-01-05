/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

/**
 * ObjectUtil
 */
public class ObjectUtil
{
	private ObjectUtil()
	{
	}

	/**
	 * Compares two objects of the same type for equality taking into account that none, one, or both may be null
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static <T> boolean areNotEqual(T s1, T s2)
	{
		return (s1 == null) ? (s2 != null) : (s2 == null) ? true : !s1.equals(s2);
	}

	/**
	 * Compares two objects of the same type for equality taking into account that none, one, or both may be null
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static <T> boolean areEqual(T s1, T s2)
	{
		return (s1 == null) ? (s2 == null) : (s2 != null) ? s1.equals(s2) : false;
	}
}
