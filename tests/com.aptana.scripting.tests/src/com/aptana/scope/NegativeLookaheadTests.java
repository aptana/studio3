/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

public class NegativeLookaheadTests
{
	/**
	 * testLookaheadMatches
	 */
	@Test
	public void testLookaheadMatches()
	{
		IScopeSelector selector = new ScopeSelector("A B - C");

		assertFalse(selector.matches("A B C"));
		assertTrue(selector.matches("A B"));
	}

	/**
	 * testLookaheadDoesNotMatch
	 */
	@Test
	public void testLookaheadDoesNotMatch()
	{
		IScopeSelector selector = new ScopeSelector("A B - C");

		assertTrue(selector.matches("A B D"));
	}

	/**
	 * testLookaheadAgainstNothing
	 */
	@Test
	public void testLookaheadAgainstNothing()
	{
		IScopeSelector selector = new ScopeSelector("A B - C");

		assertTrue(selector.matches("A B"));
	}

	/**
	 * testMultipleLookahead
	 */
	@Test
	public void testMultipleLookahead()
	{
		IScopeSelector selector = new ScopeSelector("A B - C D");

		assertTrue(selector.matches("A B"));
		assertTrue(selector.matches("A B C"));
		assertFalse(selector.matches("A B C D"));
		assertFalse(selector.matches("A B C E D"));
		assertTrue(selector.matches("A B C E"));
	}

	@Test
	public void testAS3_894()
	{
		IScopeSelector selector = new ScopeSelector("source -meta.source.embedded");

		assertTrue(selector.matches("text.html.basic source.css.embedded.html"));
	}
}
