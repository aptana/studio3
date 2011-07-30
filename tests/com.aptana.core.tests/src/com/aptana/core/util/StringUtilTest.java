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

	public void testReplaceAllWithNull()
	{
		assertNull(StringUtil.replaceAll(null, new HashMap<String, String>()));

		String template = "_replace_ me";
		assertEquals(template, StringUtil.replaceAll(template, null));
		assertEquals(template, StringUtil.replaceAll(template, new HashMap<String, String>()));
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

	public void testTokenizeWithNull()
	{
		assertEquals(0, StringUtil.tokenize(null, "\0").size());
	}

	public void testAreNotEqual()
	{
		assertFalse(StringUtil.areNotEqual(null, null));
		assertTrue(StringUtil.areNotEqual(null, "test"));
		assertTrue(StringUtil.areNotEqual("test", null));
		assertFalse(StringUtil.areNotEqual("test", "test"));
		assertTrue(StringUtil.areNotEqual("test", "tes"));
	}

	public void testAreEqual()
	{
		assertTrue(StringUtil.areEqual(null, null));
		assertFalse(StringUtil.areEqual(null, "test"));
		assertFalse(StringUtil.areEqual("test", null));
		assertTrue(StringUtil.areEqual("test", "test"));
		assertFalse(StringUtil.areEqual("test", "tes"));
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

	public void testGetStringValueWithNull()
	{
		assertSame(StringUtil.EMPTY, StringUtil.getStringValue(null));
	}

	public void testGetStringValueWithString()
	{
		String text = "abc";

		assertSame(text, StringUtil.getStringValue(text));
	}

	public void testGetStringValueWithObject()
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

	public void testCharacterInstanceCount()
	{
		String text = "how_many_character_one_has";

		assertEquals(4, StringUtil.characterInstanceCount(text, 'a'));
		assertEquals(0, StringUtil.characterInstanceCount(text, 'g'));
	}

	public void testCharacterInstanceCountWithNull()
	{
		assertEquals(-1, StringUtil.characterInstanceCount(null, 'a'));
	}

	public void testContains()
	{
		String[] array = new String[] { "test", "test1", "test2" };

		assertTrue(StringUtil.contains(array, "test"));
		assertFalse(StringUtil.contains(array, "test3"));
		assertFalse(StringUtil.contains(array, null));
	}

	public void testContainsWithNull()
	{
		assertFalse(StringUtil.contains(null, "test"));
	}

	public void testEllipsify()
	{
		String text = "Ellipsify";

		assertEquals(text + "...", StringUtil.ellipsify(text));
		assertNull(StringUtil.ellipsify(null));
	}

	public void testFormatWithIntReplacement()
	{
		String text = "replace integer {0}";

		assertEquals("replace integer 0", StringUtil.format(text, 0));
	}

	public void testFormatWithLongReplacement()
	{
		String text = "replace long {0}";

		assertEquals("replace long 987654321", StringUtil.format(text, 987654321l));
	}

	public void testJoinCollection()
	{
		Collection<String> test = new ArrayList<String>();
		test.add("test");
		test.add("test1");

		assertEquals("test&test1", StringUtil.join("&", test));
	}

	public void testJoinList()
	{
		List<String> test = new ArrayList<String>();
		test.add("test");
		test.add("test1");

		assertEquals("test&test1", StringUtil.join("&", test));
	}

	public void testJoinListWithNull()
	{
		assertNull(StringUtil.join("&", (List<String>) null));
	}

	public void testJoinEmptyList()
	{
		assertEquals(StringUtil.EMPTY, StringUtil.join("&", new ArrayList<String>()));
	}

	public void testJoinArray()
	{
		String[] test = new String[] { "test", "test1" };

		assertEquals("test&test1", StringUtil.join("&", test));
	}

	public void testJoinArrayWithNull()
	{
		assertNull(StringUtil.join("&", (String[]) null));
	}

	public void testJoinEmptyArray()
	{
		assertEquals(StringUtil.EMPTY, StringUtil.join("&", new String[0]));
	}

	public void testMakeFormLabel()
	{
		String text = "label";

		assertEquals(text + ":", StringUtil.makeFormLabel(text));
		assertNull(StringUtil.makeFormLabel(null));
	}

	public void testQuote()
	{
		String text = "quote";

		assertEquals("'quote'", StringUtil.quote(text));
		assertNull(StringUtil.quote(null));
	}

	public void testReplace()
	{
		String text = "this##is##replace##test";

		assertEquals("this is replace test", StringUtil.replace(text, "##", " "));
		assertNull(StringUtil.replace(null, "##", "@"));
	}

	public void testTruncate()
	{
		String text = "truncate test";

		assertEquals("truncate...", StringUtil.truncate(text, 8));
		assertEquals(text, StringUtil.truncate(text, 14));
		assertNull(StringUtil.truncate(null, 8));
	}
}
