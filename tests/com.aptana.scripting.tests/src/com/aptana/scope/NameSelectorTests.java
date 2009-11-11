package com.aptana.scope;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NameSelectorTests
{
	@Test
	public void nameIsPrefix()
	{
		NameSelector name = new NameSelector("source");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertTrue(selector.matches("source.ruby"));
	}
	
	@Test
	public void nameIsIdentical()
	{
		NameSelector name = new NameSelector("source.ruby");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertTrue(selector.matches("source.ruby"));
	}
	
	@Test
	public void nameIsPartial()
	{
		NameSelector name = new NameSelector("sourc");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches("source.ruby"));
	}
	
	@Test
	public void nameIsEmpty()
	{
		NameSelector name = new NameSelector("");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches("source.ruby"));
	}
	
	@Test
	public void scopeIsEmpty()
	{
		NameSelector name = new NameSelector("source");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches(""));
	}
	
	@Test
	public void nameIsNull()
	{
		NameSelector name = new NameSelector(null);
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches("source.ruby"));
	}
	
	@Test
	public void scopeIsNull()
	{
		NameSelector name = new NameSelector("source");
		ScopeSelector selector = new ScopeSelector(name);
		
		assertFalse(selector.matches(null));
	}
}
