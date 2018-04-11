/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.epl.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

public class SoftHashMapTest
{
	private static final int ONE_MB = 1024 * 1024; // 1 MB

	@Test
	public void testSoftHashMap() throws Exception
	{
		// This test grows our memory until the values are pruned (or we'd get an out of memory error).
		Map<Integer, byte[]> softHashMap = new SoftHashMap<Integer, byte[]>();
		for (int i = 0; i < Integer.MAX_VALUE; i++)
		{
			softHashMap.put(i, new byte[ONE_MB]);

			int notFound = 0;
			int found = 0;
			for (int j = 0; j < i; j++)
			{
				Object o = softHashMap.get(j);
				if (o == null)
				{
					notFound++;
				}
				else
				{
					found++;
				}
			}
			// if (notFound > 0)
			// {
			// System.out.println("Not found: " + notFound + " Found: " + found + " Loop: " + i);
			// }
			if (notFound > found)
			{
				assertTrue(i > 1);
				return;
			}
		}
		fail("Expected soft hash map to reduce memory at some point.");
	}

}
