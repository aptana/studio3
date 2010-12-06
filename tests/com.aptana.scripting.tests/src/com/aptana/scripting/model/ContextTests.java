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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextTests extends BundleTestBase
{
	/**
	 * getCommand
	 * 
	 * @return
	 */
	private CommandElement getCommand()
	{
		// load bundle
		String bundleName = "contexts";
		BundleEntry entry = this.getBundleEntry(bundleName, BundlePrecedence.PROJECT);
		
		// get commands
		List<CommandElement> commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.size());
		
		// return the command we're interested in
		return commands.get(0);
	}
	
	/**
	 * getContextMap
	 * 
	 * @return
	 */
	private Map<String, Object> getContextMap()
	{
		// get the command we're interested in
		CommandElement command = this.getCommand();
		
		// create a new context and grab its map
		CommandContext context = command.createCommandContext();
		Map<String, Object> map = context.getMap();
		assertNotNull(map);
		
		return map;
	}
	
	/**
	 * testContextContributor
	 */
	public void testContextContributor()
	{
		// grab the context's map
		Map<String, Object> map = this.getContextMap();
		
		// make sure our context contributor added its test property
		assertTrue(map.containsKey(TestContextContributor.TEST_PROPERTY));

		// make sure the test property is the type we contributed
		Object value = map.get(TestContextContributor.TEST_PROPERTY);
		assertTrue(value instanceof TestEnvironmentContributor);
	}
	
	/**
	 * testContextToEnvironment
	 */
	public void testContextToEnvironment()
	{
		// get the command we're interested in
		CommandElement command = this.getCommand();
		
		// create a new context and grab its map
		CommandContext context = command.createCommandContext();
		Map<String, Object> contextMap = context.getMap();
		assertNotNull(contextMap);
		
		Map<String, String> environment = new HashMap<String, String>();
		command.populateEnvironment(contextMap, environment);
		
		for (int i = 0; i < TestEnvironmentContributor.TEST_VARIABLES.length; i++)
		{
			String variableName = TestEnvironmentContributor.TEST_VARIABLES[i]; 
				
			assertTrue(environment.containsKey(variableName));
			assertEquals(TestEnvironmentContributor.TEST_VALUES[i], environment.get(variableName));
		}
	}
	
	/**
	 * testRubyToEnvironment
	 */
	public void testRubyToEnvironment()
	{
		String propertyName = "test_property";
		String propertyValue = "test_property value";
		
		// get the command we're interested in
		CommandElement command = this.getCommand();
		
		// create a new context and grab its map
		CommandContext context = command.createCommandContext();
		Map<String, Object> contextMap = context.getMap();
		assertNotNull(contextMap);
		
		Map<String, String> environment = new HashMap<String, String>();
		command.populateEnvironment(contextMap, environment);
		
		assertTrue(environment.containsKey(propertyName));
		assertEquals(propertyValue, environment.get(propertyName));
	}
}
