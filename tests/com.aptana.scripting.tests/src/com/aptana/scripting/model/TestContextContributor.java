package com.aptana.scripting.model;

import org.jruby.Ruby;
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

		if (command != null)
		{
			Ruby runtime = command.getRuntime();

			if (runtime != null)
			{
				IRubyObject value = ScriptUtils.instantiateClass(runtime, "ContextContributor");

				if (value != null)
				{
					context.put("test2", value);
				}
			}
		}
	}
}
