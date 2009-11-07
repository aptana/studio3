package com.aptana.scope;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OrSelectorTests
{
	@Test
	public void prefixThenNonMatch()
	{
		ScopeSelector selector = new ScopeSelector("source, string.quoted.single.ruby");

		assertTrue(selector.matches("source.ruby"));
		assertFalse(selector.matches("string.quoted.double.ruby"));
	}

	@Test
	public void nonMatchThenPrefix()
	{
		ScopeSelector selector = new ScopeSelector("source.php, string.quoted");

		assertFalse(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}

	@Test
	public void namesArePrefixes()
	{
		ScopeSelector selector = new ScopeSelector("source, string.quoted");

		assertTrue(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}

	@Test
	public void exactThenNonMatch()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby, string.quoted.single.ruby");

		assertTrue(selector.matches("source.ruby"));
		assertFalse(selector.matches("string.quoted.double.ruby"));
	}

	@Test
	public void nonMatchThenExact()
	{
		ScopeSelector selector = new ScopeSelector("source.php, string.quoted.double.ruby");

		assertFalse(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}

	@Test
	public void namesAreExact()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby, string.quoted.double.ruby");

		assertTrue(selector.matches("source.ruby"));
		assertTrue(selector.matches("string.quoted.double.ruby"));
	}
	
	@Test
	public void mixedMatch()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby string, source.php string");
		
		assertTrue(selector.matches("source.ruby string"));
		assertTrue(selector.matches("source.php string"));
		assertTrue(selector.matches("source.ruby string.quoted"));
		assertTrue(selector.matches("source.php string.quoted"));
	}
}
