/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.util.List;

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
		List<CommandElement> commands = BundleTestBase.getBundleManagerInstance().getExecutableCommands(filter);
		
		assertNotNull(commands);
		assertEquals(1, commands.size());
		assertEquals("Ruby", commands.get(0).getDisplayName());
	}

	/**
	 * testHasTriggerFilter
	 */
	public void testHasTriggerFilter()
	{
		HasTriggerFilter filter = new HasTriggerFilter();
		List<CommandElement> commands = BundleTestBase.getBundleManagerInstance().getExecutableCommands(filter);
		
		assertNotNull(commands);
		assertEquals(3, commands.size());
		assertEquals("HTML", commands.get(0).getDisplayName());
		assertEquals("JS", commands.get(1).getDisplayName());
		assertEquals("CSS String", commands.get(2).getDisplayName());
	}

	/**
	 * testAndFilter
	 */
	public void testAndFilter()
	{
		ScopeFilter scopeFilter = new ScopeFilter("source.js");
		HasTriggerFilter hasTriggerFilter = new HasTriggerFilter();
		AndFilter filter = new AndFilter(scopeFilter, hasTriggerFilter);
		
		List<CommandElement> commands = BundleTestBase.getBundleManagerInstance().getExecutableCommands(filter);
		
		assertNotNull(commands);
		assertEquals(1, commands.size());
		assertEquals("JS", commands.get(0).getDisplayName());
	}
	
	/**
	 * testOrFilter
	 */
	public void testOrFilter()
	{
		ScopeFilter scopeFilter1 = new ScopeFilter("source.ruby");
		ScopeFilter scopeFilter2 = new ScopeFilter("source.js");
		OrFilter filter = new OrFilter(scopeFilter1, scopeFilter2);
		
		List<CommandElement> commands = BundleTestBase.getBundleManagerInstance().getExecutableCommands(filter);
		
		assertNotNull(commands);
		assertEquals(2, commands.size());
		assertEquals("Ruby", commands.get(0).getDisplayName());
		assertEquals("JS", commands.get(1).getDisplayName());
	}
	
	/**
	 * testNotFilter
	 */
	public void testNotFilter()
	{
		HasTriggerFilter hasTriggerFilter = new HasTriggerFilter();
		NotFilter filter = new NotFilter(hasTriggerFilter);
		
		List<CommandElement> commands = BundleTestBase.getBundleManagerInstance().getExecutableCommands(filter);
		
		assertNotNull(commands);
		assertEquals(1, commands.size());
		assertEquals("Ruby", commands.get(0).getDisplayName());
	}
}
