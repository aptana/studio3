/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
<<<<<<< HEAD
=======
import org.junit.experimental.categories.Category;
>>>>>>> a3695a42df... test: remove performance tests

public class StringUtilTest
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

	@Test
	public void testMd5()
	{
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", StringUtil.md5(""));
		assertEquals("a4c4da98a897d052baf31d4e5c0cce55", StringUtil.md5("cwilliams@aptana.com"));

		assertNull(StringUtil.md5(null));
	}

	@Test
	public void testSanitizeHTML()
	{
		assertEquals("Heckle &amp; Jeckle", StringUtil.sanitizeHTML("Heckle & Jeckle"));
	}

	@Test
	public void testSanitizeHTML2()
	{
		assertEquals("&lt;html&gt;Heckle &amp; Jeckle&lt;/html&gt;",
				StringUtil.sanitizeHTML("<html>Heckle & Jeckle</html>"));
	}

	@Test
	public void testReplaceAll()
	{
		String template = "_replace_ me";
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("_replace_", "Pass");
		String result = StringUtil.replaceAll(template, variables);
		assertEquals("Pass me", result);

	}

	@Test
	public void testReplaceWithNull()
	{
		String template = "_replace_ me";
		Map<String, String> variables = new HashMap<String, String>();

		variables.put("_replace_ ", null);
		String result = StringUtil.replaceAll(template, variables);
		assertEquals("me", result);
	}

	@Test
	public void testReplaceAllHandlesDollarSignsInValues()
	{
		String template = "_replace_ me";
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("_replace_", "Pa$$");
		String result = StringUtil.replaceAll(template, variables);
		assertEquals("Pa$$ me", result);
	}

	@Test
	public void testReplaceAllReplacesMultipleInstances()
	{
		String template = "_replace_ me. _replace_!";
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("_replace_", "Pass");
		String result = StringUtil.replaceAll(template, variables);
		assertEquals("Pass me. Pass!", result);
	}

	@Test
	public void testReplaceAllWithNull()
	{
		assertNull(StringUtil.replaceAll(null, new HashMap<String, String>()));

		String template = "_replace_ me";
		assertEquals(template, StringUtil.replaceAll(template, null));
		assertEquals(template, StringUtil.replaceAll(template, new HashMap<String, String>()));
	}

	@Test
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

	@Test
	public void testTokenizeWithNull()
	{
		assertEquals(0, StringUtil.tokenize(null, "\0").size());
	}

	@Test
	public void testCompare1()
	{
		assertTrue(StringUtil.compare(null, null) == 0);
	}

	@Test
	public void testCompare2()
	{
		assertTrue(StringUtil.compare(null, "a") < 0);
	}

	@Test
	public void testCompare3()
	{
		assertTrue(StringUtil.compare("", "a") < 0);
	}

	@Test
	public void testCompare4()
	{
		assertTrue(StringUtil.compare("a", null) > 0);
	}

	@Test
	public void testCompare5()
	{
		assertTrue(StringUtil.compare("a", "") > 0);
	}

	@Test
	public void testCompare6()
	{
		assertTrue(StringUtil.compare("A", "A") == 0);
	}

	@Test
	public void testCompare7()
	{
		assertTrue(StringUtil.compare("A", "a") < 0);
	}

	@Test
	public void testCompare8()
	{
		assertTrue(StringUtil.compare("a", "a") == 0);
	}

	@Test
	public void testCompare9()
	{
		assertTrue(StringUtil.compare("b", "A") > 0);
	}

	@Test
	public void testCompare10()
	{
		assertTrue(StringUtil.compare("A", "b") < 0);
	}

	@Test
	public void testCompare11()
	{
		assertTrue(StringUtil.compare("a", "A") > 0);
	}

	@Test
	public void testCompareCaseInsensitive1()
	{
		assertTrue(StringUtil.compareCaseInsensitive(null, null) == 0);
	}

	@Test
	public void testCompareCaseInsensitive2()
	{
		assertTrue(StringUtil.compareCaseInsensitive(null, "a") < 0);
	}

	@Test
	public void testCompareCaseInsensitive3()
	{
		assertTrue(StringUtil.compareCaseInsensitive("", "a") < 0);
	}

	@Test
	public void testCompareCaseInsensitive4()
	{
		assertTrue(StringUtil.compareCaseInsensitive("a", null) > 0);
	}

	@Test
	public void testCompareCaseInsensitive5()
	{
		assertTrue(StringUtil.compareCaseInsensitive("a", "") > 0);
	}

	@Test
	public void testCompareCaseInsensitive6()
	{
		assertTrue(StringUtil.compareCaseInsensitive("A", "A") == 0);
	}

	@Test
	public void testCompareCaseInsensitive7()
	{
		assertTrue(StringUtil.compareCaseInsensitive("A", "a") == 0);
	}

	@Test
	public void testCompareCaseInsensitive8()
	{
		assertTrue(StringUtil.compareCaseInsensitive("a", "a") == 0);
	}

	@Test
	public void testCompareCaseInsensitive9()
	{
		assertTrue(StringUtil.compareCaseInsensitive("a", "A") == 0);
	}

	@Test
	public void testCompareCaseInsensitive10()
	{
		assertTrue(StringUtil.compareCaseInsensitive("b", "A") > 0);
	}

	@Test
	public void testCompareCaseInsensitive11()
	{
		assertTrue(StringUtil.compareCaseInsensitive("A", "b") < 0);
	}

	@Test
	public void testGetStringValueWithNull()
	{
		assertSame(StringUtil.EMPTY, StringUtil.getStringValue(null));
	}

	@Test
	public void testGetStringValueWithString()
	{
		String text = "abc";

		assertSame(text, StringUtil.getStringValue(text));
	}

	@Test
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

	@Test
	public void testCharacterInstanceCount()
	{
		String text = "how_many_character_one_has";

		assertEquals(4, StringUtil.characterInstanceCount(text, 'a'));
		assertEquals(0, StringUtil.characterInstanceCount(text, 'g'));
	}

	@Test
	public void testCharacterInstanceCountWithNull()
	{
		assertEquals(-1, StringUtil.characterInstanceCount(null, 'a'));
	}

	@Test
	public void testContains()
	{
		String[] array = new String[] { "test", "test1", "test2" };

		assertTrue(StringUtil.contains(array, "test"));
		assertFalse(StringUtil.contains(array, "test3"));
		assertFalse(StringUtil.contains(array, null));
	}

	@Test
	public void testContainsWithNull()
	{
		assertFalse(StringUtil.contains(null, "test"));
	}

	@Test
	public void testEllipsify()
	{
		String text = "Ellipsify";

		assertEquals(text + "...", StringUtil.ellipsify(text));
		assertNull(StringUtil.ellipsify(null));
	}

	@Test
	public void testJoinCollection()
	{
		Collection<String> test = CollectionsUtil.newList("test", "test1");

		assertEquals("test&test1", StringUtil.join("&", test));
	}

	@Test
	public void testJoinList()
	{
		Collection<String> test = CollectionsUtil.newList("test", "test1");

		assertEquals("test&test1", StringUtil.join("&", test));
	}

	@Test
	public void testJoinListWithNull()
	{
		assertNull(StringUtil.join("&", (List<String>) null));
	}

	@Test
	public void testJoinEmptyList()
	{
		assertEquals(StringUtil.EMPTY, StringUtil.join("&", new ArrayList<String>()));
	}

	@Test
	public void testJoinArray()
	{
		String[] test = new String[] { "test", "test1" };

		assertEquals("test&test1", StringUtil.join("&", test));
	}

	@Test
	public void testJoinCharArray()
	{
		char[] test = new char[] { 'a', 'b', 'c' };

		assertEquals("a b c", StringUtil.join(" ", test));
	}

	@Test
	public void testJoinArrayWithNull()
	{
		assertNull(StringUtil.join("&", (String[]) null));
	}

	@Test
	public void testJoinEmptyArray()
	{
		assertEquals(StringUtil.EMPTY, StringUtil.join("&", new String[0]));
	}

	@Test
	public void testMakeFormLabel()
	{
		String text = "label";

		assertEquals(text + ":", StringUtil.makeFormLabel(text));
		assertNull(StringUtil.makeFormLabel(null));
	}

	@Test
	public void testQuote()
	{
		String text = "quote";

		assertEquals("'quote'", StringUtil.quote(text));
		assertNull(StringUtil.quote(null));
	}

	@Test
	public void testReplace()
	{
		String text = "this##is##replace##test";

		assertEquals("this is replace test", StringUtil.replace(text, "##", " "));
		assertNull(StringUtil.replace(null, "##", "@"));
	}

	@Test
	public void testTruncate()
	{
		String text = "truncate test";

		assertEquals("truncate...", StringUtil.truncate(text, 8));
		assertEquals(text, StringUtil.truncate(text, 14));
		assertNull(StringUtil.truncate(null, 8));
	}

	@Test
	public void testStartsWith()
	{
		String text = "starts with";
		assertTrue(text, StringUtil.startsWith(text, 's'));
		assertFalse(text, StringUtil.startsWith(text, 'c'));
		assertFalse(StringUtil.startsWith("", 'c'));
		assertFalse(StringUtil.startsWith(null, 'c'));
	}

	@Test
	public void testEmptyString()
	{
		assertTrue(StringUtil.isEmpty(null));
	}

	@Test
	public void testVoidString()
	{
		assertTrue(StringUtil.isEmpty(""));
		assertTrue(StringUtil.isEmpty(StringUtil.EMPTY));
	}

	@Test
	public void testPad()
	{
		assertEquals("", StringUtil.pad(null, 0, ' '));
		assertEquals(" ", StringUtil.pad(null, 1, ' '));
		assertEquals("a", StringUtil.pad("a", 0, ' '));
		assertEquals("a", StringUtil.pad("a", 1, ' '));
		assertEquals(" a", StringUtil.pad("a", 2, ' '));
	}

	@Test
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

	@Test
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

	@Test
	public void testRepeat() throws Exception
	{
		assertEquals("ttt", StringUtil.repeat('t', 3));
	}

	@Test
	public void testSplit() throws Exception
	{
		String[] split = StringUtil.split("aaa bb  ", ' ').toArray(new String[0]);
		assertTrue(Arrays.equals(new String[] { "aaa", "bb" }, split));

		split = StringUtil.split("|||", '|').toArray(new String[0]);
		assertTrue(Arrays.equals(new String[] {}, split));

		split = StringUtil.split("|a||", '|').toArray(new String[0]);
		assertTrue(Arrays.equals(new String[] { "a" }, split));

		split = StringUtil.split("  aaa  bb   ", ' ').toArray(new String[0]);
		assertTrue(Arrays.equals(new String[] { "aaa", "bb" }, split));

		split = StringUtil.split("aaa  bb", ' ').toArray(new String[0]);
		assertTrue(Arrays.equals(new String[] { "aaa", "bb" }, split));

		split = StringUtil.split("aaa  bb  ", ' ').toArray(new String[0]);
		assertTrue(Arrays.equals(new String[] { "aaa", "bb" }, split));

		split = StringUtil.split("aaa ", ' ').toArray(new String[0]);
		assertTrue(Arrays.equals(new String[] { "aaa" }, split));

		split = StringUtil.split(" aaa", ' ').toArray(new String[0]);
		assertTrue(Arrays.equals(new String[] { "aaa" }, split));

		split = StringUtil.split("aaa", ' ').toArray(new String[0]);
		assertTrue(Arrays.equals(new String[] { "aaa" }, split));

		split = StringUtil.split(" aaa   ", ' ').toArray(new String[0]);
		assertTrue(Arrays.equals(new String[] { "aaa" }, split));
	}

	@Test
	public void testDotFirst() throws Exception
	{
		assertEquals("aa", StringUtil.dotFirst("aa.bb"));
		assertEquals("", StringUtil.dotFirst(".bb"));
		assertEquals("aa", StringUtil.dotFirst("aa."));
		assertEquals("aa", StringUtil.dotFirst("aa..."));
		assertEquals("", StringUtil.dotFirst(""));
		assertEquals("aa", StringUtil.dotFirst("aa"));
	}

	@Test
	public void testIndexOf() throws Exception
	{
		assertEquals(0, StringUtil.indexOf("abcdabcd", 0, 'a'));
		assertEquals(-1, StringUtil.indexOf("abcdabcd", 0, 'z'));
		assertEquals(4, StringUtil.indexOf("abcdabcd", 1, 'a'));
		assertEquals(-1, StringUtil.indexOf("abcdabcd", 5, 'a'));
		assertEquals("abcdabcd".indexOf('a', -1), StringUtil.indexOf("abcdabcd", -1, 'a'));
		assertEquals("abcdabcd".indexOf('a', 100), StringUtil.indexOf("abcdabcd", 100, 'a'));

		assertEquals(2, StringUtil.indexOf("abcdabcd", 0, 'c', 'd'));
		assertEquals(-1, StringUtil.indexOf("abcdabcd", 0, 'z', 'x', 'y'));
		assertEquals(3, StringUtil.indexOf("abcdabcd", 1, 'a', 'd'));
		assertEquals(3, StringUtil.indexOf("abcdabcd", 1, 'x', 'd'));
	}

	@Test
	public void testLastIndexOf() throws Exception
	{
		assertEquals("Should find from fromIndex inclusively", 0, StringUtil.lastIndexOf("abcdabcd", 0, 'a'));
		assertEquals(-1, StringUtil.lastIndexOf("abcdabcd", 0, 'z'));
		assertEquals(0, StringUtil.lastIndexOf("abcdabcd", 3, 'a'));
		assertEquals(-1, StringUtil.lastIndexOf("abcdabcd", -1, 'a'));
		assertEquals("abcdabcd".lastIndexOf('a', -1), StringUtil.lastIndexOf("abcdabcd", -1, 'a'));
		assertEquals("abcdabcd".lastIndexOf('a', 100), StringUtil.lastIndexOf("abcdabcd", 100, 'a'));

		assertEquals(0, StringUtil.lastIndexOf("abcdabcd", 0, 'a', 'c'));
		assertEquals(-1, StringUtil.lastIndexOf("abcdabcd", 0, 'z', 'x', 'y'));
		assertEquals(4, StringUtil.lastIndexOf("abcdabcd", 6, 'a', 'd'));
		assertEquals("search should be inclusive of index given", 7, StringUtil.lastIndexOf("abcdabcd", 7, 'a', 'd'));
		assertEquals("search should be inclusive of index given", 7, StringUtil.lastIndexOf("abcdabcd", 7, 'x', 'd'));
		assertEquals(3, StringUtil.lastIndexOf("abcdabcd", 6, 'x', 'd'));
	}
}
