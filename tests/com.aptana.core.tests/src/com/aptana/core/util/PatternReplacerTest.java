/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import com.aptana.core.IMap;
import com.aptana.core.util.replace.RegexPatternReplacer;
import com.aptana.core.util.replace.SimpleTextPatternReplacer;

/**
 * PatternReplacerTest
 */
public class PatternReplacerTest
{
	@Test
	public void testSimpleTextPatternReplacer()
	{
		String text = "<p>This is line one.</p><p>This is line <b>two</b></p>.";
		String expected = "This is line one.This is line <i>two</i>.";

		// build replacer
		SimpleTextPatternReplacer replacer = new SimpleTextPatternReplacer();
		replacer.addPattern("<p>");
		replacer.addPattern("</p>");
		replacer.addPattern("<b>", "<i>");
		replacer.addPattern("</b>", "</i>");

		// replace
		String actual = replacer.searchAndReplace(text);

		// test result
		assertEquals(expected, actual);
	}

	@Test
	public void testRegexPatternReplacer()
	{
		String text = "This is a type name One.Two.Three: <One.Two.Three>";
		String expected = "This is a type name One.Two.Three: <b>One.Two.Three</b>";

		// build replacer
		RegexPatternReplacer replacer = new RegexPatternReplacer();
		replacer.addPattern("<[A-Za-z]+(?:\\.[A-Za-z]+)*>", new IMap<String, String>()
		{
			public String map(String item)
			{
				String type = item.substring(1, item.length() - 1);

				return "<b>".concat(type).concat("</b>");
			}
		});

		// replace
		String actual = replacer.searchAndReplace(text);

		// test result
		assertEquals(expected, actual);
	}
}
