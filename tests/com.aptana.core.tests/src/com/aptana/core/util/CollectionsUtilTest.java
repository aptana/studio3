/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

public class CollectionsUtilTest extends TestCase
{

	public void testRemoveDuplicates() throws Exception
	{
		Integer[] array = { 0, 1, 1, 2, 3, 3, 3 };
		List<Integer> list = new ArrayList<Integer>();
		for (Integer element : array)
		{
			list.add(element);
		}
		CollectionsUtil.removeDuplicates(list);
		for (int i = 0; i < list.size(); ++i)
		{
			assertEquals(i, list.get(i).intValue());
		}
	}

	public void testGetNonOverlapping() throws Exception
	{
		List<Integer> coll1 = new ArrayList<Integer>();
		coll1.add(1);
		coll1.add(2);
		coll1.add(3);
		coll1.add(4);
		coll1.add(5);

		List<Integer> coll2 = new ArrayList<Integer>();
		coll2.add(3);
		coll2.add(4);
		coll2.add(5);
		coll2.add(6);

		Collection<Integer> result = CollectionsUtil.getNonOverlapping(coll1, coll2);
		assertEquals(3, result.size());
		assertTrue(result.contains(1));
		assertTrue(result.contains(2));
		assertTrue(result.contains(6));
		assertFalse(result.contains(3));
		assertFalse(result.contains(4));
		assertFalse(result.contains(5));
	}

	public void testNullListValue()
	{
		List<String> list = CollectionsUtil.getListValue(null);

		assertNotNull(list);
		assertEquals(0, list.size());
	}

	public void testListValue()
	{
		List<String> list = new ArrayList<String>();
		List<String> result = CollectionsUtil.getListValue(list);

		assertSame(list, result);
	}

	public void testIsEmptyWithNull()
	{
		assertTrue(CollectionsUtil.isEmpty(null));
	}

	public void testIsEmptyWithEmptyList()
	{
		List<String> list = new ArrayList<String>();

		assertTrue(CollectionsUtil.isEmpty(list));
	}

	public void testIsEmptyWithNonEmptyList()
	{
		List<String> list = new ArrayList<String>();

		list.add("abc");

		assertFalse(CollectionsUtil.isEmpty(list));
	}
}
