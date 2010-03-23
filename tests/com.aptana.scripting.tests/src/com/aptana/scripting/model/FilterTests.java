package com.aptana.scripting.model;

import com.aptana.scripting.model.filters.AndFilter;
import com.aptana.scripting.model.filters.HasTriggerFilter;
import com.aptana.scripting.model.filters.NotFilter;
import com.aptana.scripting.model.filters.OrFilter;
import com.aptana.scripting.model.filters.ScopeFilter;

public class FilterTests extends BundleTestBase
{
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.loadBundleEntry("modelFilters", BundlePrecedence.PROJECT);
	}

	/**
	 * testScopeFilter
	 */
	public void testScopeFilter()
	{
		ScopeFilter filter = new ScopeFilter("source.ruby");
		CommandElement[] commands = BundleTestBase.getBundleManagerInstance().getCommands(filter);
		
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("Ruby", commands[0].getDisplayName());
	}

	/**
	 * testHasTriggerFilter
	 */
	public void testHasTriggerFilter()
	{
		HasTriggerFilter filter = new HasTriggerFilter();
		CommandElement[] commands = BundleTestBase.getBundleManagerInstance().getCommands(filter);
		
		assertNotNull(commands);
		assertEquals(3, commands.length);
		assertEquals("HTML", commands[0].getDisplayName());
		assertEquals("JS", commands[1].getDisplayName());
		assertEquals("CSS String", commands[2].getDisplayName());
	}

	/**
	 * testAndFilter
	 */
	public void testAndFilter()
	{
		ScopeFilter scopeFilter = new ScopeFilter("source.js");
		HasTriggerFilter hasTriggerFilter = new HasTriggerFilter();
		AndFilter filter = new AndFilter(scopeFilter, hasTriggerFilter);
		
		CommandElement[] commands = BundleTestBase.getBundleManagerInstance().getCommands(filter);
		
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("JS", commands[0].getDisplayName());
	}
	
	/**
	 * testOrFilter
	 */
	public void testOrFilter()
	{
		ScopeFilter scopeFilter1 = new ScopeFilter("source.ruby");
		ScopeFilter scopeFilter2 = new ScopeFilter("source.js");
		OrFilter filter = new OrFilter(scopeFilter1, scopeFilter2);
		
		CommandElement[] commands = BundleTestBase.getBundleManagerInstance().getCommands(filter);
		
		assertNotNull(commands);
		assertEquals(2, commands.length);
		assertEquals("Ruby", commands[0].getDisplayName());
		assertEquals("JS", commands[1].getDisplayName());
	}
	
	/**
	 * testNotFilter
	 */
	public void testNotFilter()
	{
		HasTriggerFilter hasTriggerFilter = new HasTriggerFilter();
		NotFilter filter = new NotFilter(hasTriggerFilter);
		
		CommandElement[] commands = BundleTestBase.getBundleManagerInstance().getCommands(filter);
		
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("Ruby", commands[0].getDisplayName());
	}
}
