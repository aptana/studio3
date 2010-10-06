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
}
