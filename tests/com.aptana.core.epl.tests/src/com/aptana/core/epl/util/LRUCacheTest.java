/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.epl.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class LRUCacheTest
{

	class SizedEntry implements ILRUCacheable
	{

		private int size;

		public SizedEntry(int size)
		{
			this.size = size;
		}

		public int getCacheFootprint()
		{
			return this.size;
		}

	}

	@Test
	public void testLRU() throws Exception
	{
		LRUCache<String, SizedEntry> cache = new LRUCache<String, SizedEntry>();
		cache.setSpaceLimit(10);
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
	}

	private void assertKeysEqual(Set<String> keys, String... expected)
	{
		assertEquals(new HashSet<String>(Arrays.asList(expected)), keys);
	}

}
