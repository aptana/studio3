/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.epl.util;

import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class LRUCacheWithSoftPrunedValuesTest
{

	class SizedEntry implements ILRUCacheable
	{

		private final int size;
		private final Object data;

		public SizedEntry(int size)
		{
			this(size, null);
		}

		public SizedEntry(int size, Object data)
		{
			this.size = size;
			this.data = data;
		}

		public int getCacheFootprint()
		{
			return this.size;
		}

	}

	@Test
	public void testLRUWithSoftPrunnedValues() throws Exception
	{
		HashMap<String, SizedEntry> auxiliaryCache = new HashMap<String, SizedEntry>();
		LRUCacheWithSoftPrunedValues<String, SizedEntry> cache = new LRUCacheWithSoftPrunedValues<String, SizedEntry>(
				10, auxiliaryCache);
		cache.put("1", new SizedEntry(5));
		cache.put("2", new SizedEntry(5));

		assertEquals(cache.getCurrentSpace(), 10);
		assertKeysEqual(cache.keys(), "1", "2");

		cache.put("3", new SizedEntry(6));
		assertEquals(cache.getCurrentSpace(), 6);
		assertKeysEqual(cache.keys(), "3");

		// as putting it would exceed the max size, it's not added at all.
		cache.put("4", new SizedEntry(22));
		assertKeysEqual(cache.keys(), "3");
		assertEquals(cache.getCurrentSpace(), 6);
		assertKeysEqual(auxiliaryCache.keySet(), "1", "2", "4");

		cache.put("5", new SizedEntry(4));
		assertEquals(cache.getCurrentSpace(), 10);
		assertKeysEqual(cache.keys(), "3", "5");
		assertKeysEqual(auxiliaryCache.keySet(), "1", "2", "4");

		cache.get("3"); // that's so that 3 remains and 5 is collected.
		cache.put("6", new SizedEntry(4));
		assertEquals(cache.getCurrentSpace(), 10);
		assertKeysEqual(cache.keys(), "3", "6");
		assertKeysEqual(auxiliaryCache.keySet(), "1", "2", "4", "5");

		// Just update time stamp
		cache.put("3", new SizedEntry(4));
		cache.put("6", new SizedEntry(4));

		assertEquals(cache.getCurrentSpace(), 8);
		assertKeysEqual(cache.keys(), "3", "6");
		assertKeysEqual(auxiliaryCache.keySet(), "1", "2", "4", "5");

		// Getting an item from the auxiliary should put it back into the LRU (and remove the oldest: in this case:
		// "3").
		SizedEntry sizedEntry = cache.get("1");
		assertEquals(sizedEntry.getCacheFootprint(), 5);
		assertEquals(cache.getCurrentSpace(), 9);
		assertKeysEqual(cache.keys(), "1", "6");
		assertKeysEqual(auxiliaryCache.keySet(), "2", "3", "4", "5");
		
		cache.put("2", new SizedEntry(1));
		assertEquals(cache.getCurrentSpace(), 10);
		assertKeysEqual(cache.keys(), "1", "2", "6");
		assertKeysEqual(auxiliaryCache.keySet(), "3", "4", "5");

		cache.flush();
		assertEquals(cache.getCurrentSpace(), 0);
		assertEquals(0, cache.keys().size());
		assertEquals(0, auxiliaryCache.size());
	}

	@Test
	public void testLRUWithSoftCache() throws Exception
	{
		// Check the soft map
		LRUCacheWithSoftPrunedValues<Integer, SizedEntry> cache = new LRUCacheWithSoftPrunedValues<Integer, SizedEntry>(
				3);

		// Getting private field with reflection
		Field field = LRUCacheWithSoftPrunedValues.class.getDeclaredField("auxiliaryCache");
		field.setAccessible(true);
		SoftHashMap<Integer, SizedEntry> softHashMap = (SoftHashMap<Integer, SizedEntry>) field.get(cache);

		cache.put(-3, new SizedEntry(1));
		cache.put(-2, new SizedEntry(1));
		cache.put(-1, new SizedEntry(1));

		try
		{
			for (int i = 0; i < Integer.MAX_VALUE; i++)
			{
				cache.put(i, new SizedEntry(1, new byte[1024 * 1024])); // 1 MB
				assertEquals(3, cache.getCurrentSpace()); // Space is always the same
				int previousSize = softHashMap.size();
				softHashMap.removeStaleEntries();
				if (previousSize > softHashMap.size())
				{
					return;
				}
			}
		}
		catch (OutOfMemoryError e)
		{
			cache = null; //if the soft hash map does not work properly clear it and fail 'gracefully' later.
		}
		fail("Expected soft hash map to reduce memory at some point.");
	}

	private void assertKeysEqual(Set<String> keys, String... expected)
	{
		assertEquals(new HashSet<String>(Arrays.asList(expected)), keys);
	}

}
