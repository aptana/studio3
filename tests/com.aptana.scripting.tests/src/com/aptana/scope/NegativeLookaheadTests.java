package com.aptana.scope;

import junit.framework.TestCase;

public class NegativeLookaheadTests extends TestCase
{
	/**
	 * testLookaheadMatches
	 */
	public void testLookaheadMatches()
	{
		ScopeSelector selector = new ScopeSelector("A B - C");

		assertFalse(selector.matches("A B C"));
	}

	/**
	 * testLookaheadDoesNotMatch
	 */
	public void testLookaheadDoesNotMatch()
	{
		ScopeSelector selector = new ScopeSelector("A B - C");

		assertTrue(selector.matches("A B D"));
	}

	/**
	 * testLookaheadAgainstNothing
	 */
	public void testLookaheadAgainstNothing()
	{
		ScopeSelector selector = new ScopeSelector("A B - C");

		assertTrue(selector.matches("A B"));
	}
	
	/**
	 * testMultipleLookahead
	 */
	public void testMultipleLookahead()
	{
		ScopeSelector selector = new ScopeSelector("A B - C D");
		
		assertTrue(selector.matches("A B"));
		assertTrue(selector.matches("A B C"));
		assertFalse(selector.matches("A B C D"));
	}
	
	public void testAS3_894()
	{
		ScopeSelector selector = new ScopeSelector("source -meta.source.embedded");

		assertTrue(selector.matches("text.html.basic source.css.embedded.html"));
	}
}
