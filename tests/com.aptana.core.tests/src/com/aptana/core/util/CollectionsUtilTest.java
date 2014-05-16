/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;

public class CollectionsUtilTest
{
	private IMap<Object, String> toStringMap = new IMap<Object, String>()
	{
		public String map(Object item)
		{
			return (item != null) ? item.toString() : StringUtil.EMPTY;
		}
	};

	@Test
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

	@Test
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

	@Test
	public void testNullListValue()
	{
		List<String> list = CollectionsUtil.getListValue(null);

		assertNotNull(list);
		assertEquals(0, list.size());
	}

	@Test
	public void testListValue()
	{
		List<String> list = new ArrayList<String>();
		List<String> result = CollectionsUtil.getListValue(list);

		assertSame(list, result);
	}

	@Test
	public void testIsEmptyWithNullList()
	{
		assertTrue(CollectionsUtil.<List<?>> isEmpty(null));
	}

	@Test
	public void testIsEmptyWithNullMap()
	{
		assertTrue(CollectionsUtil.<Map<?, ?>> isEmpty(null));
	}

	@Test
	public void testIsEmptyWithEmptyList()
	{
		List<String> list = new ArrayList<String>();

		assertTrue(CollectionsUtil.isEmpty(list));
	}

	@Test
	public void testIsEmptyWithNonEmptyList()
	{
		List<String> list = new ArrayList<String>();
		list.add("abc");

		assertFalse(CollectionsUtil.isEmpty(list));
	}

	@Test
	public void testNewList()
	{
		List<String> list = CollectionsUtil.newList("item1", "item2");

		assertNotNull(list);
		assertEquals("The list should have only two items", 2, list.size());
		assertEquals("'item1' should be at index 0", 0, list.indexOf("item1"));
		assertEquals("'item2' should be at index 1", 1, list.indexOf("item2"));
	}

	@Test
	public void testNewListNullItems()
	{
		String[] items = null;
		List<String> list = CollectionsUtil.newList(items);

		assertNotNull(list);
		assertTrue("List should be empty", list.isEmpty());
	}

	@Test
	public void testAddToListSubclass()
	{
		Number doubleOne = 1.0;
		Number intOne = 1;
		Float floatOne = 1.0f;

		// generate initial set
		List<Number> list = CollectionsUtil.newList(doubleOne, intOne);

		// add sub-type of Number
		CollectionsUtil.addToList(list, floatOne);

		assertEquals("The list should have only three items", 3, list.size());
		assertEquals("Double 1.0 should be at index 0", 0, list.indexOf(doubleOne));
		assertEquals("Integer 1 should be at index 1", 1, list.indexOf(intOne));
		assertEquals("Float 1.0f should be at index 2", 2, list.indexOf(floatOne));
	}

	@Test
	public void testAddToList()
	{
		List<String> list = CollectionsUtil.newList("a", "b");
		assertNotNull(list);

		CollectionsUtil.addToList(list, "c");
		assertEquals("The list should have only three items", 3, list.size());
		assertEquals("'a' should be at index 0", 0, list.indexOf("a"));
		assertEquals("'b' should be at index 1", 1, list.indexOf("b"));
		assertEquals("'c' should be at index 2", 2, list.indexOf("c"));
	}

	@Test
	public void testAddToListNullItems()
	{
		List<String> list = CollectionsUtil.newList("a", "b");
		assertNotNull(list);

		String[] items = null;
		CollectionsUtil.addToList(list, items);
		assertEquals("The list should have only three items", 2, list.size());
		assertEquals("'a' should be at index 0", 0, list.indexOf("a"));
		assertEquals("'b' should be at index 1", 1, list.indexOf("b"));
	}

	@Test
	public void testAddToListNullList()
	{
		try
		{
			CollectionsUtil.addToList(null, "a", "b", "c");
		}
		catch (Throwable t)
		{
			fail(t.getMessage());
		}
	}

	@Test
	public void testAddToListListSubclass()
	{
		Number doubleOne = 1.0;
		Number intOne = 1;
		Float floatOne = 1.0f;

		// generate initial set
		List<Number> list = CollectionsUtil.newList(doubleOne, intOne);

		// add sub-type of Number
		CollectionsUtil.addToList(list, CollectionsUtil.newList(floatOne));

		assertEquals("The list should have only three items", 3, list.size());
		assertEquals("Double 1.0 should be at index 0", 0, list.indexOf(doubleOne));
		assertEquals("Integer 1 should be at index 1", 1, list.indexOf(intOne));
		assertEquals("Float 1.0f should be at index 2", 2, list.indexOf(floatOne));
	}

	@Test
	public void testAddToListList()
	{
		List<String> list = CollectionsUtil.newList("a", "b");
		assertNotNull(list);

		CollectionsUtil.addToList(list, CollectionsUtil.newList("c"));
		assertEquals("The list should have only three items", 3, list.size());
		assertEquals("'a' should be at index 0", 0, list.indexOf("a"));
		assertEquals("'b' should be at index 1", 1, list.indexOf("b"));
		assertEquals("'c' should be at index 2", 2, list.indexOf("c"));
	}

	@Test
	public void testAddToListListNullItems()
	{
		List<String> list = CollectionsUtil.newList("a", "b");
		assertNotNull(list);

		List<String> items = null;
		CollectionsUtil.addToList(list, items);
		assertEquals("The list should have only three items", 2, list.size());
		assertEquals("'a' should be at index 0", 0, list.indexOf("a"));
		assertEquals("'b' should be at index 1", 1, list.indexOf("b"));
	}

	@Test
	public void testAddToListListNullList()
	{
		try
		{
			CollectionsUtil.addToList(null, CollectionsUtil.newList("a", "b", "c"));
		}
		catch (Throwable t)
		{
			fail(t.getMessage());
		}
	}

	@Test
	public void testNewSet()
	{
		Set<String> set = CollectionsUtil.newSet("item1", "item2");

		assertNotNull(set);
		assertEquals("The set should have only two items", 2, set.size());
		assertTrue("'item1' should exist in the set", set.contains("item1"));
		assertTrue("'item2' should exist in the set", set.contains("item2"));
	}

	@Test
	public void testNewSetNullItems()
	{
		String[] items = null;
		Set<String> set = CollectionsUtil.newSet(items);

		assertNotNull(set);
		assertTrue("Set should be empty", set.isEmpty());
	}

	@Test
	public void testAddToSetSubclass()
	{
		Number doubleOne = 1.0;
		Number intOne = 1;
		Float floatOne = 1.0f;

		// generate initial set
		Set<Number> set = CollectionsUtil.newSet(doubleOne, intOne);

		// add sub-type of Number
		CollectionsUtil.addToSet(set, floatOne);

		assertEquals("The set should have only three items", 3, set.size());
		assertTrue("Set should contain double 1.0", set.contains(doubleOne));
		assertTrue("Set should contain integer 1", set.contains(intOne));
		assertTrue("Set should contain float 1.0f", set.contains(floatOne));
	}

	@Test
	public void testAddToSet()
	{
		Set<String> set = CollectionsUtil.newSet("a", "b");
		assertNotNull(set);

		CollectionsUtil.addToSet(set, "c");
		assertEquals("The set should have only three items", 3, set.size());
		assertTrue("'a' should exist in the set", set.contains("a"));
		assertTrue("'b' should exist in the set", set.contains("b"));
		assertTrue("'c' should exist in the set", set.contains("c"));
	}

	@Test
	public void testAddToSetNullItems()
	{
		Set<String> set = CollectionsUtil.newSet("a", "b");
		assertNotNull(set);

		String[] items = null;
		CollectionsUtil.addToSet(set, items);
		assertEquals("The set should have only two items", 2, set.size());
		assertTrue("'a' should exist in the set", set.contains("a"));
		assertTrue("'b' should exist in the set", set.contains("b"));
	}

	@Test
	public void testAddToSetNullSet()
	{
		try
		{
			CollectionsUtil.addToSet(null, "a", "b", "c");
		}
		catch (Throwable t)
		{
			fail(t.getMessage());
		}
	}

	@Test
	public void testNewInOrderSet()
	{
		Set<String> list = new LinkedHashSet<String>();
		assertEquals(list, CollectionsUtil.newInOrderSet((String[]) null));

		list.add("item1");
		list.add("item2");
		assertEquals(list, CollectionsUtil.newInOrderSet("item1", "item2"));
	}

	@Test
	public void testCollectionFind()
	{
		List<String> list = CollectionsUtil.newList("a", "ab", "ba", "b", "bc", "cb");
		IFilter<String> selectWithA = new IFilter<String>()
		{
			public boolean include(String item)
			{
				return (item != null && item.contains("a"));
			}
		};
		String found = CollectionsUtil.find(list, selectWithA);

		assertNotNull(found);
		assertEquals("Should have found first 'a'", "a", found);
	}

	@Test
	public void testCollectionFind2()
	{
		List<String> list = CollectionsUtil.newList("a", "ab", "ba", "b", "bc", "cb");
		IFilter<String> selectWithA = new IFilter<String>()
		{
			public boolean include(String item)
			{
				return (item != null && item.startsWith("b"));
			}
		};
		String found = CollectionsUtil.find(list, selectWithA);

		assertNotNull(found);
		assertEquals("Should have found 'ba'", "ba", found);
	}

	@Test
	public void testCollectionFindNullCollection()
	{
		IFilter<String> selectWithA = new IFilter<String>()
		{
			public boolean include(String item)
			{
				return (item != null && item.contains("a"));
			}
		};
		String found = CollectionsUtil.find(null, selectWithA);

		assertNull(found);
	}

	@Test
	public void testCollectionFindNullFilter()
	{
		List<String> list = CollectionsUtil.newList("a", "ab", "ba", "b", "bc", "cb");
		String found = CollectionsUtil.find(list, null);

		assertNull(found);
	}

	@Test
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

	@Test
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

	@Test
	public void testCollectionFilterNullFilter()
	{
		List<String> list = CollectionsUtil.newList("a", "ab", "ba", "b", "bc", "cb");
		List<String> filteredList = CollectionsUtil.filter(list, null);

		assertNotNull(filteredList);
		assertEquals("List should contain 6 items", 6, filteredList.size());
	}

	@Test
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

	@Test
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

	@Test
	public void testCollectionFilterInPlaceNullFilter()
	{
		List<String> list = CollectionsUtil.newList("a", "ab", "ba", "b", "bc", "cb");
		CollectionsUtil.filterInPlace(list, null);

		assertEquals("List should contain 6 items", 6, list.size());
	}

	@Test
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

	@Test
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

	@Test
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

	@Test
	public void testMapObjectToString()
	{
		List<Integer> numbers = CollectionsUtil.newList(1, 2, 3);
		assertNotNull(numbers);
		assertEquals(3, numbers.size());

		List<String> strings = CollectionsUtil.map(numbers, toStringMap);
		assertNotNull(strings);
		assertEquals(numbers.size(), strings.size());
		assertEquals("'1' should be at index 0", 0, strings.indexOf("1"));
		assertEquals("'2' should be at index 0", 1, strings.indexOf("2"));
		assertEquals("'3' should be at index 0", 2, strings.indexOf("3"));
	}

	@Test
	public void testMapNullCollection()
	{
		Collection<Object> items = null;
		List<String> strings = CollectionsUtil.map(items, toStringMap);

		assertNotNull(strings);
		assertTrue(strings.isEmpty());
	}

	@Test
	public void testMapNullMapper()
	{
		List<Integer> numbers = CollectionsUtil.newList(1, 2, 3);
		List<String> strings = CollectionsUtil.map(numbers, null);

		assertNotNull(strings);
		assertTrue(strings.isEmpty());
	}

	@Test
	public void testMapNullSource()
	{
		Collection<Object> source = null;
		List<String> destination = new ArrayList<String>();

		CollectionsUtil.map(source, destination, toStringMap);

		assertTrue(destination.isEmpty());
	}

	@Test
	public void testMapNullDestination()
	{
		List<Integer> source = CollectionsUtil.newList(1, 2, 3);
		List<String> destination = null;

		try
		{
			CollectionsUtil.map(source, destination, toStringMap);
		}
		catch (Throwable t)
		{
			fail(t.getMessage());
		}
	}

	@Test
	public void testMapNullMapper2()
	{
		List<Integer> source = CollectionsUtil.newList(1, 2, 3);
		List<String> destination = new ArrayList<String>();

		CollectionsUtil.map(source, destination, null);
		assertTrue(destination.isEmpty());
	}

	@Test
	public void testNewMap()
	{
		Map<String, String> map = CollectionsUtil.newMap("item1", "item2");

		assertNotNull(map);
		assertEquals("The map should have only one item", 1, map.size());
		assertTrue("'item1' should exist in the map", map.containsKey("item1"));
		assertTrue("'item2' should exist in the map", map.containsValue("item2"));
		assertEquals("item2", map.get("item1"));
	}

	@Test
	public void testNewMapNullItems()
	{
		String[] items = null;
		Map<String, String> map = CollectionsUtil.newMap(items);

		assertNotNull(map);
		assertTrue("Map should be empty", map.isEmpty());
	}

	@Test
	public void testNewMapUnevenItems()
	{
		try
		{
			CollectionsUtil.newMap("item1", "item2", "item3");
			fail("Failed to throw IllegalArgumentException with odd number of items");
		}
		catch (IllegalArgumentException e)
		{
			// what we're expecting
		}
	}

	@Test
	public void testNewMapDefinedTypes()
	{
		// @formatter:off
		Map<String, Integer> map = CollectionsUtil.newTypedMap(
				String.class, Integer.class,
				"item1", 1,
				"item2", 2,
				"item3", 3);
		// @formatter:on

		assertNotNull(map);
		assertEquals("The map should have three items", 3, map.size());
		assertEquals(Integer.valueOf(1), map.get("item1"));
		assertEquals(Integer.valueOf(2), map.get("item2"));
		assertEquals(Integer.valueOf(3), map.get("item3"));
	}

	@Test
	public void testNewMapDefinedTypesUnevenItems()
	{
		try
		{
			CollectionsUtil.newTypedMap(String.class, Integer.class, "item1", 1, "item3");
			fail("Failed to throw IllegalArgumentException with odd number of items");
		}
		catch (IllegalArgumentException e)
		{
			// what we're expecting
		}
	}

	@Test
	public void testAddToMapSubclass()
	{
		Number doubleOne = 1.0;
		Number intOne = 1;
		Float floatOne = 1.0f;
		Float floatTwo = 2.0f;

		// generate initial map
		Map<Number, Number> map = CollectionsUtil.newMap(doubleOne, intOne);

		// add sub-type of Number
		CollectionsUtil.addToMap(map, floatOne, floatTwo);

		assertEquals("The map should have only two items", 2, map.size());
		assertTrue("Map should contain double 1.0", map.containsKey(doubleOne));
		assertTrue("Map should contain integer 1", map.containsValue(intOne));
		assertEquals(intOne, map.get(doubleOne));
		assertTrue("Map should contain float 1.0f", map.containsKey(floatOne));
		assertTrue("Map should contain float 2.0f", map.containsValue(floatTwo));
		assertEquals(floatTwo, map.get(floatOne));
	}

	@Test
	public void testAddToMap()
	{
		Map<String, String> map = CollectionsUtil.newMap("a", "b");
		assertNotNull(map);

		CollectionsUtil.addToMap(map, "c", "d");
		assertEquals("The map should have only two items", 2, map.size());
		assertTrue("'a' should exist in the map", map.containsKey("a"));
		assertEquals("b", map.get("a"));
		assertTrue("'b' should exist in the map", map.containsValue("b"));
		assertTrue("'c' should exist in the map", map.containsKey("c"));
		assertEquals("d", map.get("c"));
		assertTrue("'d' should exist in the map", map.containsValue("d"));
	}

	@Test
	public void testAddToMapNullItems()
	{
		Map<String, String> map = CollectionsUtil.newMap("a", "b", "c", "d");
		assertNotNull(map);

		String[] items = null;
		CollectionsUtil.addToMap(map, items);
		assertEquals("The map should have only two items", 2, map.size());
		assertTrue("'a' should exist in the map", map.containsKey("a"));
		assertEquals("b", map.get("a"));
		assertTrue("'b' should exist in the map", map.containsValue("b"));
		assertTrue("'c' should exist in the map", map.containsKey("c"));
		assertEquals("d", map.get("c"));
		assertTrue("'d' should exist in the map", map.containsValue("d"));
	}

	@Test
	public void testAddToMapWithNonNullKeyMappedToNullValue()
	{
		Map<String, String> map = CollectionsUtil.newMap("a", "b", "c", "d");
		assertNotNull(map);

		CollectionsUtil.addToMap(String.class, String.class, map, "e", null);
		assertEquals("The map should have three items", 3, map.size());
		assertTrue("'a' should exist in the map", map.containsKey("a"));
		assertEquals("b", map.get("a"));
		assertTrue("'b' should exist in the map", map.containsValue("b"));
		assertTrue("'c' should exist in the map", map.containsKey("c"));
		assertEquals("d", map.get("c"));
		assertTrue("'d' should exist in the map", map.containsValue("d"));
		assertNull(map.get("e"));
	}

	@Test
	public void testAddToMapNullMap()
	{
		try
		{
			CollectionsUtil.addToMap(null, "a", "b", "c");
		}
		catch (Throwable t)
		{
			fail(t.getMessage());
		}
	}

	@Test
	public void testAddUnevenItemsToMap()
	{
		Map<String, String> map = CollectionsUtil.newMap("a", "b");
		assertNotNull(map);
		try
		{
			CollectionsUtil.addToMap(map, "c", "d", "e");
			fail("Didn't throw IllegalArgumentException when we tried to add uneven number of elements to a map.");
		}
		catch (IllegalArgumentException t)
		{
			assertTrue(true);
		}
	}

	@Test
	public void testAddUnevenItemsToMapDefinedTypes()
	{
		Map<String, Integer> map = CollectionsUtil.newTypedMap(String.class, Integer.class, "a", 1);
		assertNotNull(map);
		try
		{
			CollectionsUtil.addToMap(String.class, Integer.class, map, "c", 2, "e");
			fail("Didn't throw IllegalArgumentException when we tried to add uneven number of elements to a map.");
		}
		catch (IllegalArgumentException t)
		{
			assertTrue(true);
		}
	}

	@Test
	public void testMapFromValues()
	{
		List<Integer> numbers = CollectionsUtil.newList(1, 2, 3);

		Map<String, Integer> cache = CollectionsUtil.mapFromValues(numbers, new IMap<Integer, String>()
		{
			public String map(Integer item)
			{
				return item.toString();
			}
		});
		assertNotNull(cache);
		assertEquals(numbers.size(), cache.size());
		assertEquals(Integer.valueOf(1), cache.get("1"));
		assertEquals(Integer.valueOf(2), cache.get("2"));
		assertEquals(Integer.valueOf(3), cache.get("3"));
	}

	@Test
	public void testFirstElementEmpty()
	{
		List<String> strings = new ArrayList<String>();
		String firstElement = CollectionsUtil.getFirstElement(strings);
		assertNull(firstElement);
	}

	@Test
	public void testFirstElementNullElement()
	{
		String firstElement = CollectionsUtil.getFirstElement(CollectionsUtil.newList((String) null));
		assertNull(firstElement);
	}

	@Test
	public void testFirstElementNullList()
	{
		assertNull(CollectionsUtil.getFirstElement(null));
	}

	@Test
	public void testFirstElementList()
	{
		List<Integer> numbers = CollectionsUtil.newList(1, 2, 3);
		Integer firstElement = CollectionsUtil.getFirstElement(numbers);
		assertEquals(new Integer(1), firstElement);
	}

	@Test
	public void testCollectionPartition()
	{
		List<String> list = CollectionsUtil.newList("a", "ab", "ba", "b", "bc", "cb");
		IFilter<String> selectWithA = new IFilter<String>()
		{
			public boolean include(String item)
			{
				return (item != null && item.contains("a"));
			}
		};
		ImmutableTuple<List<String>, List<String>> partitioned = CollectionsUtil.partition(list, selectWithA);

		assertNotNull(partitioned);
		assertEquals("True list should contain 3 items", 3, partitioned.first.size());
		assertTrue("True list should contain 'a'", partitioned.first.contains("a"));
		assertTrue("True list should contain 'ab'", partitioned.first.contains("ab"));
		assertTrue("True list should contain 'ba'", partitioned.first.contains("ba"));

		assertEquals("False list should contain 3 items", 3, partitioned.second.size());
		assertTrue("False list should contain 'b'", partitioned.second.contains("b"));
		assertTrue("False list should contain 'bc'", partitioned.second.contains("bc"));
		assertTrue("False list should contain 'cb'", partitioned.second.contains("cb"));
	}
}
