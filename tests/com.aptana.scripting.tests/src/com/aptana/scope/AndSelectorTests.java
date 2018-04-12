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

public class AndSelectorTests
{
	@Test
	public void testNamesArePrefixes()
	{
		IScopeSelector selector = new ScopeSelector("source string.quoted");
		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}

	@Test
	public void testPrefixThenExact()
	{
		IScopeSelector selector = new ScopeSelector("source string.quoted.double.ruby");
		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}

	@Test
	public void testExactThenPrefix()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby string.quoted");
		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}

	@Test
	public void testNamesAreExact()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}

	@Test
	public void testFirstNonMatching()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		assertFalse(selector.matches("source.php string.quoted.double.ruby"));
	}

	@Test
	public void testSecondNonMatching()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		assertFalse(selector.matches("source.ruby string.quoted.double.php"));
	}

	@Test
	public void testEmptySelector()
	{
		IScopeSelector selector = new ScopeSelector("");
		assertFalse(selector.matches("source.ruby string.quoted.double.ruby"));
	}

	@Test
	public void testEmptyScope()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		assertFalse(selector.matches(""));
	}

	@Test
	public void testNullSelector()
	{
		IScopeSelector selector = new ScopeSelector((String) null);
		assertFalse(selector.matches("source.ruby string.quoted.double.ruby"));
	}

	@Test
	public void testNullScope()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		assertFalse(selector.matches((String) null));
	}

	@Test
	public void testBeginsWith()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby");
		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}

	@Test
	public void testWithin()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby");
		assertTrue(selector.matches(
				"text.html.ruby source.ruby.rails.embedded.html string.quoted.double.ruby punctuation.definition.string.end.ruby"));
	}

	@Test
	public void testEndsWith()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby");
		assertTrue(selector.matches("text.html.ruby source.ruby.rails.embedded.html"));
	}
}
