package com.aptana.scripting.model;

import java.util.HashMap;
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
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		
		// return the command we're interested in
		return commands[0];
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
