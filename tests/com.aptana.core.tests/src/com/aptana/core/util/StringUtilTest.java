/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class StringUtilTest extends TestCase
{
	/**
	 * Create a string by concatenating the elements of a string array using a delimited between each item
	 * 
	 * @param delimiter
	 *            The text to place between each element in the array
	 * @param items
	 *            The array of items to join
	 * @return The resulting string
	 */
	private static String oldJoin(String delimiter, String... items)
	{
		if (items == null)
		{
			return null;
		}

		int length = items.length;
		String result = StringUtil.EMPTY;

		if (length > 0)
		{
			StringBuilder sb = new StringBuilder();
			String item;

			for (int i = 0; i < length - 1; i++)
			{
				item = items[i];

				if (item != null)
				{
					sb.append(item);
				}

				sb.append(delimiter);
			}

			item = items[length - 1];

			if (item != null)
			{
				sb.append(item);
			}

			result = sb.toString();
		}

		return result;
	}

	public void testMd5()
	{
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", StringUtil.md5(""));
		assertEquals("a4c4da98a897d052baf31d4e5c0cce55", StringUtil.md5("cwilliams@aptana.com"));

		assertNull(StringUtil.md5(null));
	}

	public void testSanitizeHTML()
	{
		assertEquals("Heckle &amp; Jeckle", StringUtil.sanitizeHTML("Heckle & Jeckle"));
	}

	public void testSanitizeHTML2()
	{
		assertEquals("&lt;html&gt;Heckle &amp; Jeckle&lt;/html&gt;",
				StringUtil.sanitizeHTML("<html>Heckle & Jeckle</html>"));
	}

	public void testReplaceAll()
	{
		String template = "_replace_ me";
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("_replace_", "Pass");
		String result = StringUtil.replaceAll(template, variables);
		assertEquals("Pass me", result);

	}

	public void testReplaceWithNull()
	{
		String template = "_replace_ me";
		Map<String, String> variables = new HashMap<String, String>();

		variables.put("_replace_ ", null);
		String result = StringUtil.replaceAll(template, variables);
		assertEquals("me", result);
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

	public void testCompare1()
	{
		assertTrue(StringUtil.compare(null, null) == 0);
	}

	public void testCompare2()
	{
		assertTrue(StringUtil.compare(null, "a") < 0);
	}

	public void testCompare3()
	{
		assertTrue(StringUtil.compare("", "a") < 0);
	}

	public void testCompare4()
	{
		assertTrue(StringUtil.compare("a", null) > 0);
	}

	public void testCompare5()
	{
		assertTrue(StringUtil.compare("a", "") > 0);
	}

	public void testCompare6()
	{
		assertTrue(StringUtil.compare("A", "A") == 0);
	}

	public void testCompare7()
	{
		assertTrue(StringUtil.compare("A", "a") < 0);
	}

	public void testCompare8()
	{
		assertTrue(StringUtil.compare("a", "a") == 0);
	}

	public void testCompare9()
	{
		assertTrue(StringUtil.compare("b", "A") > 0);
	}

	public void testCompare10()
	{
		assertTrue(StringUtil.compare("A", "b") < 0);
	}

	public void testCompare11()
	{
		assertTrue(StringUtil.compare("a", "A") > 0);
	}

	public void testCompareCaseInsensitive1()
	{
		assertTrue(StringUtil.compareCaseInsensitive(null, null) == 0);
	}

	public void testCompareCaseInsensitive2()
	{
		assertTrue(StringUtil.compareCaseInsensitive(null, "a") < 0);
	}

	public void testCompareCaseInsensitive3()
	{
		assertTrue(StringUtil.compareCaseInsensitive("", "a") < 0);
	}

	public void testCompareCaseInsensitive4()
	{
		assertTrue(StringUtil.compareCaseInsensitive("a", null) > 0);
	}

	public void testCompareCaseInsensitive5()
	{
		assertTrue(StringUtil.compareCaseInsensitive("a", "") > 0);
	}

	public void testCompareCaseInsensitive6()
	{
		assertTrue(StringUtil.compareCaseInsensitive("A", "A") == 0);
	}

	public void testCompareCaseInsensitive7()
	{
		assertTrue(StringUtil.compareCaseInsensitive("A", "a") == 0);
	}

	public void testCompareCaseInsensitive8()
	{
		assertTrue(StringUtil.compareCaseInsensitive("a", "a") == 0);
	}

	public void testCompareCaseInsensitive9()
	{
		assertTrue(StringUtil.compareCaseInsensitive("a", "A") == 0);
	}

	public void testCompareCaseInsensitive10()
	{
		assertTrue(StringUtil.compareCaseInsensitive("b", "A") > 0);
	}

	public void testCompareCaseInsensitive11()
	{
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

	public void testStartsWith()
	{
		String text = "starts with";
		assertTrue(text, StringUtil.startsWith(text, 's'));
		assertFalse(text, StringUtil.startsWith(text, 'c'));
		assertFalse(StringUtil.startsWith("", 'c'));
		assertFalse(StringUtil.startsWith(null, 'c'));
	}

	public void testEmptyString()
	{
		assertTrue(StringUtil.isEmpty(null));
	}

	public void testVoidString()
	{
		assertTrue(StringUtil.isEmpty(""));
		assertTrue(StringUtil.isEmpty(StringUtil.EMPTY));
	}

	public void testFormatString()
	{
		String text = "This is a test for {0}";
		assertEquals("This is a test for some file", StringUtil.format(text, "some file"));
	}

	public void testFormatObject()
	{
		String text = "This is a test for {0}";
		File file = new File("testfile");
		assertEquals("This is a test for testfile", StringUtil.format(text, file));
	}

	public void testFormatObjectArray()
	{
		String text = "This is a test for {0} {1}";
		File file = new File("testfile");
		File file2 = new File("testfile2");
		assertEquals("This is a test for testfile testfile2", StringUtil.format(text, new Object[] { file, file2 }));

	}

	public void testPad()
	{
		assertEquals("", StringUtil.pad(null, 0, ' '));
		assertEquals(" ", StringUtil.pad(null, 1, ' '));
		assertEquals("a", StringUtil.pad("a", 0, ' '));
		assertEquals("a", StringUtil.pad("a", 1, ' '));
		assertEquals(" a", StringUtil.pad("a", 2, ' '));
	}

	public void testfindPreviousWhitespaceOffset()
	{
		assertEquals(-1, StringUtil.findPreviousWhitespaceOffset(null, 0));
		assertEquals(-1, StringUtil.findPreviousWhitespaceOffset("", 0));
		assertEquals(-1, StringUtil.findPreviousWhitespaceOffset("", 1));
		assertEquals(-1, StringUtil.findPreviousWhitespaceOffset("a", 0));
		assertEquals(-1, StringUtil.findPreviousWhitespaceOffset("a", 1));
		assertEquals(0, StringUtil.findPreviousWhitespaceOffset(" a", 1));
		assertEquals(0, StringUtil.findPreviousWhitespaceOffset(" a", 2));
		assertEquals(1, StringUtil.findPreviousWhitespaceOffset("  a", 2));
		assertEquals(-1, StringUtil.findPreviousWhitespaceOffset("a b", 1));
		assertEquals(1, StringUtil.findPreviousWhitespaceOffset("a b", 2));
		assertEquals(1, StringUtil.findPreviousWhitespaceOffset("a b c", 2));
		assertEquals(1, StringUtil.findPreviousWhitespaceOffset("a b c", 3));
		assertEquals(3, StringUtil.findPreviousWhitespaceOffset("a b c", 4));
	}

	public void testfindNextWhitespaceOffset()
	{
		assertEquals(-1, StringUtil.findNextWhitespaceOffset(null, 0));
		assertEquals(-1, StringUtil.findNextWhitespaceOffset("", 0));
		assertEquals(-1, StringUtil.findNextWhitespaceOffset("", 1));
		assertEquals(-1, StringUtil.findNextWhitespaceOffset("a", 0));
		assertEquals(-1, StringUtil.findNextWhitespaceOffset("a", 1));
		assertEquals(0, StringUtil.findNextWhitespaceOffset(" a", 0));
		assertEquals(-1, StringUtil.findNextWhitespaceOffset(" a", 1));
		assertEquals(-1, StringUtil.findNextWhitespaceOffset(" a", 2));
		assertEquals(-1, StringUtil.findNextWhitespaceOffset("  a", 2));
		assertEquals(1, StringUtil.findNextWhitespaceOffset("a b", 1));
		assertEquals(-1, StringUtil.findNextWhitespaceOffset("a b", 2));
		assertEquals(3, StringUtil.findNextWhitespaceOffset("a b c", 2));
		assertEquals(3, StringUtil.findNextWhitespaceOffset("a b c", 3));
		assertEquals(-1, StringUtil.findNextWhitespaceOffset("a b c", 4));
	}

	public void testJoinSpeed()
	{
		// @formatter:off
		timeBothJoins(
			"(with delim)",
			"~~|~~",
			new String[] { "abc", "def", "ghi", "jkl", "mno", "pqr", "stu", "vwx", "yz" }
		);
		// @formatter:on
	}

	public void testEmptyDelimiterJoinSpeed()
	{
		// @formatter:off
		timeBothJoins(
			"(no delim)",
			null,
			new String[] { "abc", "def", "ghi", "jkl", "mno", "pqr", "stu", "vwx", "yz" }
		);
		// @formatter:on
	}

	protected void timeBothJoins(String title, String delimiter, String... items)
	{
		timeOldJoin(title, delimiter, items);
		timeNewJoin(title, delimiter, items);
	}

	protected void timeNewJoin(String title, String delimiter, String... items)
	{
		long start = System.currentTimeMillis();

		for (int i = 0; i < 1000000; i++)
		{
			StringUtil.join(delimiter, items);
		}

		long diff = System.currentTimeMillis() - start;
		System.out.println("new join " + title + ": " + diff + "ms");
	}

	protected void timeOldJoin(String title, String delimiter, String... items)
	{
		long start = System.currentTimeMillis();

		for (int i = 0; i < 1000000; i++)
		{
			oldJoin(delimiter, items);
		}

		long diff = System.currentTimeMillis() - start;
		System.out.println("old join " + title + ": " + diff + "ms");
	}

	public void testConcatVersusStringBuilder()
	{
		timeConcatArray();
		timeConcatList();
		timeStringBuilder();
	}

	protected void timeConcatList()
	{
		List<String> items = CollectionsUtil.newList("abc", "def", "ghi", "jkl", "mno", "pqr", "stu", "vwx", "yz");

		long start = System.currentTimeMillis();

		for (int i = 0; i < 1000000; i++)
		{
			String result = StringUtil.concat(items);
			assertNotNull(result);
			assertTrue(result.length() != 0);
		}

		long diff = System.currentTimeMillis() - start;

		System.out.println("concat list: " + diff + "ms");
	}

	protected void timeConcatArray()
	{
		String[] items = new String[] { "abc", "def", "ghi", "jkl", "mno", "pqr", "stu", "vwx", "yz" };

		long start = System.currentTimeMillis();

		for (int i = 0; i < 1000000; i++)
		{
			String result = StringUtil.concat(items);
			assertNotNull(result);
			assertTrue(result.length() != 0);
		}

		long diff = System.currentTimeMillis() - start;

		System.out.println("concat array: " + diff + "ms");
	}

	protected void timeStringBuilder()
	{
		List<String> items = CollectionsUtil.newList("abc", "def", "ghi", "jkl", "mno", "pqr", "stu", "vwx", "yz");

		long start = System.currentTimeMillis();

		for (int i = 0; i < 1000000; i++)
		{
			StringBuilder builder = new StringBuilder();

			for (String item : items)
			{
				builder.append(item);

			}

			String result = builder.toString();
			assertTrue(result.length() != 0);
		}

		long diff = System.currentTimeMillis() - start;

		System.out.println("string builder: " + diff + "ms");
	}
}
