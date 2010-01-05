package com.aptana.scripting.model;

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

		// context.put("test2", ScriptUtils.instantiateClass("ContextContributor"));
	}
}
