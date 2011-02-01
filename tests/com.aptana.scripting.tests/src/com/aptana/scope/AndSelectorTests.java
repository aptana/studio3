/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import junit.framework.TestCase;

public class AndSelectorTests extends TestCase
{
	/**
	 * testNamesArePrefixes
	 */
	public void testNamesArePrefixes()
	{
		IScopeSelector selector = new ScopeSelector("source string.quoted");

		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}

	/**
	 * testPrefixThenExact
	 */
	public void testPrefixThenExact()
	{
		IScopeSelector selector = new ScopeSelector("source string.quoted.double.ruby");

		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}

	/**
	 * testExactThenPrefix
	 */
	public void testExactThenPrefix()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby string.quoted");

		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}

	/**
	 * testNamesAreExact
	 */
	public void testNamesAreExact()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");

		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}

	/**
	 * testFirstNonMatching
	 */
	public void testFirstNonMatching()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");

		assertFalse(selector.matches("source.php string.quoted.double.ruby"));
	}

	/**
	 * testSecondNonMatching
	 */
	public void testSecondNonMatching()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");

		assertFalse(selector.matches("source.ruby string.quoted.double.php"));
	}

	/**
	 * testEmptySelector
	 */
	public void testEmptySelector()
	{
		IScopeSelector selector = new ScopeSelector("");

		assertFalse(selector.matches("source.ruby string.quoted.double.ruby"));
	}

	/**
	 * testEmptyScope
	 */
	public void testEmptyScope()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");

		assertFalse(selector.matches(""));
	}

	/**
	 * testNullSelector
	 */
	public void testNullSelector()
	{
		IScopeSelector selector = new ScopeSelector((String) null);

		assertFalse(selector.matches("source.ruby string.quoted.double.ruby"));
	}

	/**
	 * testNullScope
	 */
	public void testNullScope()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");

		assertFalse(selector.matches((String) null));
	}
	
	/**
	 * testBeginsWith
	 */
	public void testBeginsWith()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby");

		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}
	
	/**
	 * testWithin
	 */
	public void testWithin()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby");

		assertTrue(selector.matches("text.html.ruby source.ruby.rails.embedded.html string.quoted.double.ruby punctuation.definition.string.end.ruby"));
	}
	
	/**
	 * testEndsWith
	 */
	public void testEndsWith()
	{
		IScopeSelector selector = new ScopeSelector("source.ruby");

		assertTrue(selector.matches("text.html.ruby source.ruby.rails.embedded.html"));
	}
}
