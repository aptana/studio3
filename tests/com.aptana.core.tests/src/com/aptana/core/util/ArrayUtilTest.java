/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ArrayUtilTest
{
	@Test
	public void testFlatten() throws Exception
	{
		Integer[] array1 = { 0, 1, 2 };
		Integer[] array2 = { 3, 4, 5 };
		Integer[] array3 = { 6, 7, 8 };
		Integer[] array4 = {};

		try
		{
			ArrayUtil.flatten(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{

		}

		Integer[] combined = ArrayUtil.flatten(array1);
		assertEquals("Flattened array not of correct size. Some element missing", array1.length, combined.length);

		combined = ArrayUtil.flatten(array1, array2, array3);
		assertEquals("Flattened array not of correct size. Some element missing", array1.length + array2.length
				+ array3.length + array4.length, combined.length);
		for (int i = 0; i < combined.length; i++)
		{
			assertEquals("Value in combined array not as expected", i, (int) combined[i]);
		}
	}
}
