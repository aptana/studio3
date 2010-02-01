package com.aptana.scripting.model;

public class FilterTests extends BundleTestBase
{
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.loadBundleEntry("modelFilters", BundleScope.PROJECT);
	}

	/**
	 * testScopeFilter
	 */
	public void testScopeFilter()
	{
		ScopeFilter filter = new ScopeFilter("source.ruby");
		CommandElement[] commands = BundleManager.getInstance().getCommands(filter);
		
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("Ruby", commands[0].getDisplayName());
	}

	/**
	 * testScopeContainsFilterExact
	 */
	public void testScopeContainsFilterExact()
	{
		ScopeContainsFilter filter;
		CommandElement[] commands;

		filter = new ScopeContainsFilter("source.css string.double.quoted");
		commands = BundleManager.getInstance().getCommands(filter);

		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("CSS String", commands[0].getDisplayName());
	}

	/**
	 * testScopeContainsFilterEndsWith
	 */
	public void testScopeContainsFilterEndsWith()
	{
		ScopeContainsFilter filter;
		CommandElement[] commands;

		filter = new ScopeContainsFilter("text.html source.css string.double.quoted");
		commands = BundleManager.getInstance().getCommands(filter);

		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("CSS String", commands[0].getDisplayName());
	}

	/**
	 * testScopeContainsFilterBeginsWith
	 */
	public void testScopeContainsFilterBeginsWith()
	{
		ScopeContainsFilter filter;
		CommandElement[] commands;

		filter = new ScopeContainsFilter("text.html source.css string.double.quoted punctuation");
		commands = BundleManager.getInstance().getCommands(filter);

		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("CSS String", commands[0].getDisplayName());
	}

	/**
	 * testScopeContainsFilterNotFull
	 */
	public void testScopeContainsFilterNotFull()
	{
		ScopeContainsFilter filter;
		CommandElement[] commands;

		filter = new ScopeContainsFilter("text.html source.css");
		commands = BundleManager.getInstance().getCommands(filter);

		assertNotNull(commands);
		assertEquals(0, commands.length);
	}

	/**
	 * testScopeContainsFilterSomethingInMiddle
	 */
	public void testScopeContainsFilterSomethingInMiddle()
	{
		ScopeContainsFilter filter;
		CommandElement[] commands;

		filter = new ScopeContainsFilter("source.css punctuation string.double.quoted");
		commands = BundleManager.getInstance().getCommands(filter);

		assertNotNull(commands);
		assertEquals(0, commands.length);
	}

	/**
	 * testHasTriggerFilter
	 */
	public void testHasTriggerFilter()
	{
		HasTriggerFilter filter = new HasTriggerFilter();
		CommandElement[] commands = BundleManager.getInstance().getCommands(filter);
		
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
		
		CommandElement[] commands = BundleManager.getInstance().getCommands(filter);
		
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
		
		CommandElement[] commands = BundleManager.getInstance().getCommands(filter);
		
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
		
		CommandElement[] commands = BundleManager.getInstance().getCommands(filter);
		
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("Ruby", commands[0].getDisplayName());
	}
}
