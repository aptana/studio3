package com.aptana.scope;

import junit.framework.TestCase;

public class OrSelectorTests extends TestCase
{
	
	public void testPrefixThenNonMatch()
	{
		ScopeSelector selector = new ScopeSelector("source, string.quoted.single.ruby");

		assertTrue(selector.matches("source.ruby"));
		assertFalse(selector.matches("string.quoted.double.ruby"));
	}

	
	public void testNonMatchThenPrefix()
	{
		ScopeSelector selector = new ScopeSelector("source.php, string.quoted");

		assertFalse(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}

	
	public void testNamesArePrefixes()
	{
		ScopeSelector selector = new ScopeSelector("source, string.quoted");

		assertTrue(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}

	
	public void testExactThenNonMatch()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby, string.quoted.single.ruby");

		assertTrue(selector.matches("source.ruby"));
		assertFalse(selector.matches("string.quoted.double.ruby"));
	}

	
	public void testNonMatchThenExact()
	{
		ScopeSelector selector = new ScopeSelector("source.php, string.quoted.double.ruby");

		assertFalse(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}

	
	public void testNamesAreExact()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby, string.quoted.double.ruby");

		assertTrue(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}
	
	
	public void testMixedMatch()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby string, source.php string");
		
		assertTrue(selector.matches("source.ruby string"));
		assertTrue(selector.matches("source.php string"));
		assertTrue(selector.matches("source.ruby string.quoted"));
		assertTrue(selector.matches("source.php string.quoted"));
	}
}
