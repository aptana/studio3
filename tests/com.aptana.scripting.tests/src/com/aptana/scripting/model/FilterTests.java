/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
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
