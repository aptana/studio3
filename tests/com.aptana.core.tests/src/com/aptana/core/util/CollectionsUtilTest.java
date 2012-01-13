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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.aptana.core.IFilter;

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

	public void testNewList()
	{
		List<String> list = new ArrayList<String>();
		assertEquals(list, CollectionsUtil.newList((String[]) null));

		list.add("item1");
		list.add("item2");
		assertEquals(list, CollectionsUtil.newList("item1", "item2"));
	}

	public void testNewSet()
	{
		Set<String> list = new HashSet<String>();
		assertEquals(list, CollectionsUtil.newSet((String[]) null));

		list.add("item1");
		list.add("item2");
		assertEquals(list, CollectionsUtil.newSet("item1", "item2"));
	}

	public void testNewInOrderSet()
	{
		Set<String> list = new LinkedHashSet<String>();
		assertEquals(list, CollectionsUtil.newInOrderSet((String[]) null));

		list.add("item1");
		list.add("item2");
		assertEquals(list, CollectionsUtil.newInOrderSet("item1", "item2"));
	}

	public void testCollectionFilter()
	{
		List<String> list = CollectionsUtil.newList("a", "ab", "ba", "b", "bc", "cb");
		IFilter<String> selectWithA = new IFilter<String>()
		{
			public boolean include(String item)
			{
				return (item != null && item.contains("a"));
			}
		};
		List<String> filteredList = CollectionsUtil.filter(list, selectWithA);

		assertNotNull(filteredList);
		assertEquals("List should contain 3 items", 3, filteredList.size());
		assertTrue("List should contain 'a'", filteredList.contains("a"));
		assertTrue("List should contain 'ab'", filteredList.contains("ab"));
		assertTrue("List should contain 'ba'", filteredList.contains("ba"));
	}

	public void testCollectionFilterNullCollection()
	{
		IFilter<String> selectWithA = new IFilter<String>()
		{
			public boolean include(String item)
			{
				return (item != null && item.contains("a"));
			}
		};
		List<String> filteredList = CollectionsUtil.filter(null, selectWithA);

		assertNotNull(filteredList);
		assertEquals("List should contain 0 items", 0, filteredList.size());
	}

	public void testCollectionFilterNullFilter()
	{
		List<String> list = CollectionsUtil.newList("a", "ab", "ba", "b", "bc", "cb");
		List<String> filteredList = CollectionsUtil.filter(list, null);

		assertNotNull(filteredList);
		assertEquals("List should contain 6 items", 6, filteredList.size());
	}

	public void testCollectionFilterInPlace()
	{
		List<String> list = CollectionsUtil.newList("a", "ab", "ba", "b", "bc", "cb");
		IFilter<String> selectWithA = new IFilter<String>()
		{
			public boolean include(String item)
			{
				return (item != null && item.contains("a"));
			}
		};

		CollectionsUtil.filterInPlace(list, selectWithA);

		assertEquals("List should contain 3 items", 3, list.size());
		assertTrue("List should contain 'a'", list.contains("a"));
		assertTrue("List should contain 'ab'", list.contains("ab"));
		assertTrue("List should contain 'ba'", list.contains("ba"));
	}

	public void testCollectionFilterInPlaceNullCollection()
	{
		IFilter<String> selectWithA = new IFilter<String>()
		{
			public boolean include(String item)
			{
				return (item != null && item.contains("a"));
			}
		};

		try
		{
			CollectionsUtil.filterInPlace(null, selectWithA);
		}
		catch (Throwable t)
		{
			fail("CollectionsUtil.filterInPlace should not throw an exception with a null collection");
		}
	}

	public void testCollectionFilterInPlaceNullFilter()
	{
		List<String> list = CollectionsUtil.newList("a", "ab", "ba", "b", "bc", "cb");
		CollectionsUtil.filterInPlace(list, null);

		assertEquals("List should contain 6 items", 6, list.size());
	}

	public void testCollectionFilterWithDestinationCollection()
	{
		List<String> list1 = CollectionsUtil.newList("a", "b", "c");
		List<String> list2 = CollectionsUtil.newList("ab", "ba", "bc", "cb");
		IFilter<String> selectWithA = new IFilter<String>()
		{
			public boolean include(String item)
			{
				return (item != null && item.contains("a"));
			}
		};
		List<String> accumulator = new ArrayList<String>();

		CollectionsUtil.filter(list1, accumulator, selectWithA);
		CollectionsUtil.filter(list2, accumulator, selectWithA);

		assertNotNull(accumulator);
		assertEquals("List should contain 3 items", 3, accumulator.size());
		assertTrue("List should contain 'a'", accumulator.contains("a"));
		assertTrue("List should contain 'ab'", accumulator.contains("ab"));
		assertTrue("List should contain 'ba'", accumulator.contains("ba"));
	}

	public void testCollectionFilterWithDestinationCollectionNullSource()
	{
		IFilter<String> selectWithA = new IFilter<String>()
		{
			public boolean include(String item)
			{
				return (item != null && item.contains("a"));
			}
		};
		List<String> accumulator = new ArrayList<String>();

		try
		{
			CollectionsUtil.filter(null, accumulator, selectWithA);
		}
		catch (Throwable t)
		{
			fail("CollectionsUtil.filter should not throw an exception with a null destination collection");
		}

		assertEquals("List should contain 0 items", 0, accumulator.size());
	}

	public void testCollectionFilterWithDestinationCollectionNullFilter()
	{
		List<String> list1 = CollectionsUtil.newList("a", "b", "c");
		List<String> list2 = CollectionsUtil.newList("ab", "ba", "bc", "cb");
		List<String> accumulator = new ArrayList<String>();

		CollectionsUtil.filter(list1, accumulator, null);
		CollectionsUtil.filter(list2, accumulator, null);

		assertNotNull(accumulator);
		assertEquals("List should contain 7 items", 7, accumulator.size());
	}
}
