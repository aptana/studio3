/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OrSelectorTests
{
	@Test
	public void testPrefixThenNonMatch()
	{
		IScopeSelector selector = new ScopeSelector("source, string.quoted.single.ruby");

		assertTrue(selector.matches("source.ruby"));
		assertFalse(selector.matches("string.quoted.double.ruby"));
	}

	@Test
	public void testNonMatchThenPrefix()
	{
		IScopeSelector selector = new ScopeSelector("source.php, string.quoted");

		assertFalse(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}

	@Test
	public void testNamesArePrefixes()
	{
		IScopeSelector selector = new ScopeSelector("source, string.quoted");

		assertTrue(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}

	@Test
	public void testExactThenNonMatch()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby, string.quoted.single.ruby");

		assertTrue(selector.matches("source.ruby"));
		assertFalse(selector.matches("string.quoted.double.ruby"));
	}

	@Test
	public void testNonMatchThenExact()
	{
		IScopeSelector selector = new ScopeSelector("source.php, string.quoted.double.ruby");

		assertFalse(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}

	@Test
	public void testNamesAreExact()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby, string.quoted.double.ruby");

		assertTrue(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}

	@Test
	public void testMixedMatch()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby string, source.php string");

		assertTrue(selector.matches("source.ruby string"));
		assertTrue(selector.matches("source.php string"));
		assertTrue(selector.matches("source.ruby string.quoted"));
		assertTrue(selector.matches("source.php string.quoted"));
	}
}
