/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import junit.framework.TestCase;

public class NegativeLookaheadTests extends TestCase
{
	/**
	 * testLookaheadMatches
	 */
	public void testLookaheadMatches()
	{
		IScopeSelector selector = new ScopeSelector("A B - C");

		assertFalse(selector.matches("A B C"));
	}

	/**
	 * testLookaheadDoesNotMatch
	 */
	public void testLookaheadDoesNotMatch()
	{
		IScopeSelector selector = new ScopeSelector("A B - C");

		assertTrue(selector.matches("A B D"));
	}

	/**
	 * testLookaheadAgainstNothing
	 */
	public void testLookaheadAgainstNothing()
	{
		IScopeSelector selector = new ScopeSelector("A B - C");

		assertTrue(selector.matches("A B"));
	}

	/**
	 * testMultipleLookahead
	 */
	public void testMultipleLookahead()
	{
		IScopeSelector selector = new ScopeSelector("A B - C D");

		assertTrue(selector.matches("A B"));
		assertTrue(selector.matches("A B C"));
		assertFalse(selector.matches("A B C D"));
	}

	public void testAS3_894()
	{
		IScopeSelector selector = new ScopeSelector("source -meta.source.embedded");

		assertTrue(selector.matches("text.html.basic source.css.embedded.html"));
	}
}
