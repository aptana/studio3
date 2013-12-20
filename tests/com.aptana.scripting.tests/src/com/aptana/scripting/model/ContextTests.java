/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

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
	@Test
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
	@Test
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
	@Test
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
