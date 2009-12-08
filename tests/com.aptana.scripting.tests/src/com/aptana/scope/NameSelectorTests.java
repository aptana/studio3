package com.aptana.scope;

import junit.framework.TestCase;

public class NameSelectorTests extends TestCase
{

	public void testNameIsPrefix()
	{
		NameSelector name = new NameSelector("source");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertTrue(selector.matches("source.ruby"));
	}
	

	public void testNameIsIdentical()
	{
		NameSelector name = new NameSelector("source.ruby");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertTrue(selector.matches("source.ruby"));
	}
	

	public void testNameIsPartial()
	{
		NameSelector name = new NameSelector("sourc");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches("source.ruby"));
	}
	

	public void testNameIsEmpty()
	{
		NameSelector name = new NameSelector("");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches("source.ruby"));
	}
	

	public void testScopeIsEmpty()
	{
		NameSelector name = new NameSelector("source");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches(""));
	}
	

	public void testNameIsNull()
	{
		NameSelector name = new NameSelector(null);
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches("source.ruby"));
	}
	

	public void testScopeIsNull()
	{
		NameSelector name = new NameSelector("source");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches(null));
	}
}
