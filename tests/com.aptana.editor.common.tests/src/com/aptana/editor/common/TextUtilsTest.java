/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

public class TextUtilsTest
{

	@Test
	public void testCombine()
	{
		String[][] arrays = new String[][] { new String[] { "one", "two" }, new String[] { "two", "three" } };
		String[] result = TextUtils.combine(arrays);
		assertEquals(3, result.length);
		assertEquals("one", result[0]);
		assertEquals("two", result[1]);
		assertEquals("three", result[2]);
	}

	@Test
	public void testCombineArrays()
	{
		String[][] array1 = new String[][] { new String[] { "one", "two" }, new String[] { "two", "three" } };
		String[][] array2 = new String[][] { new String[] { "2one", "2two" }, new String[] { "2two", "2three" } };
		String[][] result = TextUtils.combineArrays(array1, array2);
		assertEquals(4, result.length);
		String[] result1 = result[0];
		assertEquals("one", result1[0]);
		assertEquals("two", result1[1]);
		String[] result2 = result[1];
		assertEquals("two", result2[0]);
		assertEquals("three", result2[1]);
		String[] result3 = result[2];
		assertEquals("2one", result3[0]);
		assertEquals("2two", result3[1]);
		String[] result4 = result[3];
		assertEquals("2two", result4[0]);
		assertEquals("2three", result4[1]);
	}

	@Test
	public void testRemoveDuplicates()
	{
		char[][] arrays = new char[][] { new char[] { 'h', 'i' }, new char[] { 't', 'h', 'e', 'r', 'e' },
				new char[] { 'h', 'i' } };
		char[][] result = TextUtils.removeDuplicates(arrays);
		assertEquals(2, result.length);
		assertEquals("hi", String.valueOf(result[0]));
		assertEquals("there", String.valueOf(result[1]));
	}
}
