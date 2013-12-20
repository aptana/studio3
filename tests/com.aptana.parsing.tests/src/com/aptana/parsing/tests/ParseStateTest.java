/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.aptana.parsing.ParseState;

/**
 * @author Fabio
 */
public class ParseStateTest
{

	@Test
	public void testParseState() throws Exception
	{
		ParseState parseState = new ParseState("test");
		Object key1 = parseState.getCacheKey("contentType");

		parseState = new ParseState("test");
		Object key1a = parseState.getCacheKey("contentType");

		parseState = new ParseState("test1234569078000-");
		Object key2 = parseState.getCacheKey("contentType");

		parseState = new ParseState("test1234569078000-");
		Object key2a = parseState.getCacheKey("contentType");

		parseState = new ParseState("test1234569078000");
		Object key3 = parseState.getCacheKey("contentType");

		assertEquals(key1, key1a);
		assertEquals(key2, key2a);
		assertEquals(key1.hashCode(), key1a.hashCode());
		assertEquals(key2.hashCode(), key2a.hashCode());

		assertFalse(key1.equals(key2));
		assertFalse(key3.equals(key2));
		assertFalse(key3.equals(key1));
	}
}
