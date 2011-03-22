/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class StringUtilTest extends TestCase
{
	public void testMd5()
	{
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", StringUtil.md5(""));
		assertEquals("a4c4da98a897d052baf31d4e5c0cce55", StringUtil.md5("cwilliams@aptana.com"));

		assertNull(StringUtil.md5(null));
	}

	public void testSanitizeHTML()
	{
		assertEquals("Heckle &amp; Jeckle", StringUtil.sanitizeHTML("Heckle & Jeckle"));
		assertEquals("&lt;html>Heckle &amp; Jeckle&lt;/html>", StringUtil.sanitizeHTML("<html>Heckle & Jeckle</html>"));
	}

	public void testReplaceAll()
	{
		String template = "_replace_ me";
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("_replace_", "Pass");
		String result = StringUtil.replaceAll(template, variables);
		assertEquals("Pass me", result);
	}

	public void testReplaceAllHandlesDollarSignsInValues()
	{
		String template = "_replace_ me";
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("_replace_", "Pa$$");
		String result = StringUtil.replaceAll(template, variables);
		assertEquals("Pa$$ me", result);
	}

	public void testReplaceAllReplacesMultipleInstances()
	{
		String template = "_replace_ me. _replace_!";
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("_replace_", "Pass");
		String result = StringUtil.replaceAll(template, variables);
		assertEquals("Pass me. Pass!", result);
	}

	public void testTokenize()
	{
		String inputString = "chris\0williams";
		List<String> tokens = StringUtil.tokenize(inputString, "\0");
		assertEquals(2, tokens.size());
		assertEquals("chris", tokens.get(0));
		assertEquals("williams", tokens.get(1));

		inputString = "chris\n williams";
		tokens = StringUtil.tokenize(inputString, "\n");
		assertEquals(2, tokens.size());
		assertEquals("chris", tokens.get(0));
		assertEquals(" williams", tokens.get(1));
	}

	public void testAreNotEqualWithNulls()
	{
		assertFalse(StringUtil.areNotEqual(null, null));
	}

	public void testAreNotEqualFirstIsNull()
	{
		assertTrue(StringUtil.areNotEqual(null, "test"));
	}

	public void testAreNotEqualLastIsNull()
	{
		assertTrue(StringUtil.areNotEqual("test", null));
	}

	public void testAreNotEqual()
	{
		assertFalse(StringUtil.areNotEqual("test", "test"));
	}

	public void testAreNotEqual2()
	{
		assertTrue(StringUtil.areNotEqual("test", "tes"));
	}

	public void testCompare()
	{
		assertTrue(StringUtil.compare(null, null) == 0);
		assertTrue(StringUtil.compare(null, "a") < 0);
		assertTrue(StringUtil.compare("", "a") < 0);
		assertTrue(StringUtil.compare("a", null) > 0);
		assertTrue(StringUtil.compare("a", "") > 0);
		assertTrue(StringUtil.compare("A", "A") == 0);
		assertTrue(StringUtil.compare("A", "a") < 0);
		assertTrue(StringUtil.compare("a", "a") == 0);
		assertTrue(StringUtil.compare("a", "A") > 0);
		assertTrue(StringUtil.compare("b", "A") > 0);
		assertTrue(StringUtil.compare("A", "b") < 0);
	}

	public void testCompareCaseInsensitive()
	{
		assertTrue(StringUtil.compareCaseInsensitive(null, null) == 0);
		assertTrue(StringUtil.compareCaseInsensitive(null, "a") < 0);
		assertTrue(StringUtil.compareCaseInsensitive("", "a") < 0);
		assertTrue(StringUtil.compareCaseInsensitive("a", null) > 0);
		assertTrue(StringUtil.compareCaseInsensitive("a", "") > 0);
		assertTrue(StringUtil.compareCaseInsensitive("A", "A") == 0);
		assertTrue(StringUtil.compareCaseInsensitive("A", "a") == 0);
		assertTrue(StringUtil.compareCaseInsensitive("a", "a") == 0);
		assertTrue(StringUtil.compareCaseInsensitive("a", "A") == 0);
		assertTrue(StringUtil.compareCaseInsensitive("b", "A") > 0);
		assertTrue(StringUtil.compareCaseInsensitive("A", "b") < 0);
	}

	public void getStringValueWithNull()
	{
		assertSame(StringUtil.EMPTY, StringUtil.getStringValue(null));
	}

	public void getStringValueWithString()
	{
		String text = "abc";

		assertSame(text, StringUtil.getStringValue(text));
	}

	public void getStringValueWithObject()
	{
		final String text = "hello";
		Object item = new Object()
		{
			public String toString()
			{
				return text;
			}
		};

		assertSame(text, StringUtil.getStringValue(item));
	}
}
