package com.aptana.scope;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class AndSelectorTests
{
	@Test
	public void namesArePrefixes()
	{
		ScopeSelector selector = new ScopeSelector("source string.quoted");
		
		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}
	
	@Test
	public void prefixThenExact()
	{
		ScopeSelector selector = new ScopeSelector("source string.quoted.double.ruby");
		
		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}
	
	@Test
	public void exactThenPrefix()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby string.quoted");
		
		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}
	
	@Test
	public void namesAreExact()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		
		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}
	
	@Test
	public void firstNonMatching()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		
		assertFalse(selector.matches("source.php string.quoted.double.ruby"));
	}
	
	@Test
	public void secondNonMatching()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		
		assertFalse(selector.matches("source.ruby string.quoted.double.php"));
	}
	
	@Test
	public void emptySelector()
	{
		ScopeSelector selector = new ScopeSelector("");
		
		assertFalse(selector.matches("source.ruby string.quoted.double.ruby"));
	}
	
	@Test
	public void emptyScope()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		
		assertFalse(selector.matches(""));
	}
	
	@Test
	public void nullSelector()
	{
		ScopeSelector selector = new ScopeSelector((String) null);
		
		assertFalse(selector.matches("source.ruby string.quoted.double.ruby"));
	}
	
	@Test
	public void nullScope()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		
		assertFalse(selector.matches(null));
	}
}
