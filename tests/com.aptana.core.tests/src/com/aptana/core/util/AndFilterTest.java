/**
 * Aptana Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.IFilter;

public class AndFilterTest
{

	private AndFilter<Integer> and;
	private IFilter<Integer> evenFilter = new IFilter<Integer>()
	{
		public boolean include(Integer item)
		{
			return item != null && item % 2 == 0;
		};
	};
	private IFilter<Integer> oddFilter = new IFilter<Integer>()
	{
		public boolean include(Integer item)
		{
			return item != null && item % 2 == 1;
		};
	};
	private IFilter<Integer> divisibleByThreeFilter = new IFilter<Integer>()
	{
		public boolean include(Integer item)
		{
			return item != null && item % 3 == 0;
		};
	};
	private List<Integer> collection;

	@Before
	public void setUp() throws Exception
	{
		collection = CollectionsUtil.newList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	@After
	public void tearDown() throws Exception
	{
		and = null;
		collection = null;
	}

	@Test
	public void testEvensAndDivisibleByThree()
	{
		and = new AndFilter<Integer>(evenFilter, divisibleByThreeFilter);
		List<Integer> result = CollectionsUtil.filter(collection, and);
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(0, result.get(0).intValue());
		assertEquals(6, result.get(1).intValue());
	}

	@Test
	public void testEvensAndOdds()
	{
		and = new AndFilter<Integer>(evenFilter, oddFilter);
		List<Integer> result = CollectionsUtil.filter(collection, and);
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testNullArgument()
	{
		try
		{
			and = new AndFilter<Integer>(null);
			fail("Expected to throw IllegalArgumentException with null arg");
		}
		catch (IllegalArgumentException e)
		{
			// expected
		}
	}

}
