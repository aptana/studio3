/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class ExpiringMapTests
{

	private static final int LONG_TIMEOUT = 60000 * 5; // 5 minutes
	private static final int SHORT_TIMEOUT = 200; // 200 ms

	@Test
	public void testPutAll()
	{
		ExpiringMap<String, String> map = setUpExpirationMap(LONG_TIMEOUT);

		Map<String, String> insert = new HashMap<String, String>();
		insert.put("three", "fish");
		insert.put("four", "bird");

		map.putAll(insert);
		assertNotNull("Failed to insert map into expiration map", map.get("four"));
	}

	@Test
	public void testGetItem()
	{
		ExpiringMap<String, String> map = setUpExpirationMap(LONG_TIMEOUT);
		assertTrue("Failed to get item", map.get("one").equals("cat"));
	}

	@Test
	public void testRemoveItem()
	{
		ExpiringMap<String, String> map = setUpExpirationMap(LONG_TIMEOUT);
		map.remove("two");
		assertNull("Failed to remove item", map.get("two"));
	}

	@Test
	public void testExpiredItem() throws InterruptedException
	{
		ExpiringMap<String, String> map = setUpExpirationMap(SHORT_TIMEOUT);
		Thread.sleep(SHORT_TIMEOUT + 50);
		assertNull("An expired item was retrieved", map.get("two"));
	}

	@Test
	public void testRemoveExpiredItem() throws InterruptedException
	{
		ExpiringMap<String, String> map = setUpExpirationMap(SHORT_TIMEOUT);
		Thread.sleep(SHORT_TIMEOUT + 50);
		map.remove("two");
		assertNull("Failed to remove expired item", map.get("two"));
	}

	@Test
	public void testClearMap()
	{
		ExpiringMap<String, String> map = setUpExpirationMap(LONG_TIMEOUT);
		map.clear();
		assertNull("Failed to clear map", map.get("one"));
	}

	@Test
	public void testKeySet()
	{
		ExpiringMap<String, String> map = setUpExpirationMap(LONG_TIMEOUT);
		Set<String> keySet = map.keySet();
		assertTrue("Could not find valid keys in keyset.", keySet.contains("one") && keySet.contains("two"));
	}

	@Test
	public void testIsEmpty()
	{
		ExpiringMap<String, String> map = setUpExpirationMap(LONG_TIMEOUT);
		boolean hasException = false;

		try
		{
			map.isEmpty();
		}
		catch (UnsupportedOperationException e)
		{
			hasException = true;
		}
		assertTrue(hasException);
	}

	@Test
	public void testContainsKey()
	{
		ExpiringMap<String, String> map = setUpExpirationMap(LONG_TIMEOUT);
		boolean hasException = false;

		try
		{
			map.containsKey("two");
		}
		catch (UnsupportedOperationException e)
		{
			hasException = true;
		}
		assertTrue(hasException);
	}

	@Test
	public void testContainsValue()
	{
		ExpiringMap<String, String> map = setUpExpirationMap(LONG_TIMEOUT);
		boolean hasException = false;

		try
		{
			map.containsValue("cat");
		}
		catch (UnsupportedOperationException e)
		{
			hasException = true;
		}
		assertTrue(hasException);
	}

	@Test
	public void testEntrySet()
	{
		ExpiringMap<String, String> map = setUpExpirationMap(LONG_TIMEOUT);
		boolean hasException = false;

		try
		{
			map.entrySet();
		}
		catch (UnsupportedOperationException e)
		{
			hasException = true;
		}
		assertTrue(hasException);
	}

	@Test
	public void testSize()
	{
		ExpiringMap<String, String> map = setUpExpirationMap(LONG_TIMEOUT);
		boolean hasException = false;

		try
		{
			map.size();
		}
		catch (UnsupportedOperationException e)
		{
			hasException = true;
		}
		assertTrue(hasException);

	}

	@Test
	public void testValues()
	{
		ExpiringMap<String, String> map = setUpExpirationMap(LONG_TIMEOUT);
		boolean hasException = false;

		try
		{
			map.values();
		}
		catch (UnsupportedOperationException e)
		{
			hasException = true;
		}
		assertTrue(hasException);
	}

	@Test
	public void testInsertEmptyValues()
	{
		ExpiringMap<String, String> map = setUpExpirationMap(LONG_TIMEOUT);
		map.put("three", StringUtil.EMPTY);
		assertTrue("Failed to insert empty values", map.get("three").equals(StringUtil.EMPTY));
	}

	@Test
	public void testInsertNullValues()
	{
		ExpiringMap<String, String> map = setUpExpirationMap(LONG_TIMEOUT);
		map.put("three", null);
		assertNull("Failed to insert item with null value", map.get("three"));
	}

	private ExpiringMap<String, String> setUpExpirationMap(int timeout)
	{
		ExpiringMap<String, String> expirationMap = new ExpiringMap<String, String>(timeout);
		expirationMap.put("one", "cat");
		expirationMap.put("two", "dog");
		return expirationMap;
	}

}
