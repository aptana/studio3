package com.aptana.scripting.model;

import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.scripting.ScriptUtils;

public class TestContextContributor implements ContextContributor
{
	public static final String TEST_PROPERTY = "test_property";

	/**
	 * BlockEnvironmentContributor
	 */
	public TestContextContributor()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.ContextContributor#modifyContext(com.aptana.scripting.model.CommandElement,
	 * com.aptana.scripting.model.CommandContext)
	 */
	public void modifyContext(CommandElement command, CommandContext context)
	{
		context.put(TEST_PROPERTY, new TestEnvironmentContributor());
		
		// We have to load a specific bundle in order for ContextContributor to exist, so
		// we don't want to do anything when it doesn't exist.
		IRubyObject value = ScriptUtils.instantiateClass("ContextContributor");
		
		if (value != null)
		{
			context.put("test2", value);
		}
	}
}
