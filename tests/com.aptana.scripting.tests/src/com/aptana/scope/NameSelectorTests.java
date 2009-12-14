package com.aptana.scope;

import junit.framework.TestCase;

public class NameSelectorTests extends TestCase
{
	/**
	 * testNameIsPrefix
	 */
	public void testNameIsPrefix()
	{
		NameSelector name = new NameSelector("source");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertTrue(selector.matches("source.ruby"));
	}
	
	/**
	 * testNameIsIdentical
	 */
	public void testNameIsIdentical()
	{
		NameSelector name = new NameSelector("source.ruby");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertTrue(selector.matches("source.ruby"));
	}
	
	/**
	 * testNameIsPartial
	 */
	public void testNameIsPartial()
	{
		NameSelector name = new NameSelector("sourc");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches("source.ruby"));
	}
	
	/**
	 * testNameIsEmpty
	 */
	public void testNameIsEmpty()
	{
		NameSelector name = new NameSelector("");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches("source.ruby"));
	}
	
	/**
	 * testScopeIsEmpty
	 */
	public void testScopeIsEmpty()
	{
		NameSelector name = new NameSelector("source");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches(""));
	}
	
	/**
	 * testNameIsNull
	 */
	public void testNameIsNull()
	{
		NameSelector name = new NameSelector(null);
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches("source.ruby"));
	}
	
	/**
	 * testScopeIsNull
	 */
	public void testScopeIsNull()
	{
		NameSelector name = new NameSelector("source");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches((String) null));
	}
}
