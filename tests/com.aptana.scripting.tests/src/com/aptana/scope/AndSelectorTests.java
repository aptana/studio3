package com.aptana.scope;

import junit.framework.TestCase;


public class AndSelectorTests extends TestCase
{
	public void testNamesArePrefixes()
	{
		ScopeSelector selector = new ScopeSelector("source string.quoted");
		
		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}
	
	public void testPrefixThenExact()
	{
		ScopeSelector selector = new ScopeSelector("source string.quoted.double.ruby");
		
		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}
	
	public void testExactThenPrefix()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby string.quoted");
		
		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}
	
	public void testNamesAreExact()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		
		assertTrue(selector.matches("source.ruby string.quoted.double.ruby"));
	}
	
	public void testFirstNonMatching()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		
		assertFalse(selector.matches("source.php string.quoted.double.ruby"));
	}
	
	public void testSecondNonMatching()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		
		assertFalse(selector.matches("source.ruby string.quoted.double.php"));
	}
	
	public void testEmptySelector()
	{
		ScopeSelector selector = new ScopeSelector("");
		
		assertFalse(selector.matches("source.ruby string.quoted.double.ruby"));
	}
	
	public void testEmptyScope()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		
		assertFalse(selector.matches(""));
	}
	
	public void testNullSelector()
	{
		ScopeSelector selector = new ScopeSelector((String) null);
		
		assertFalse(selector.matches("source.ruby string.quoted.double.ruby"));
	}
	
	public void testNullScope()
	{
		ScopeSelector selector = new ScopeSelector("source.ruby string.quoted.double.ruby");
		
		assertFalse(selector.matches(null));
	}
}
