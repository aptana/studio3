package com.aptana.scripting.model;

public interface ContextContributor
{
	/**
	 * modifyContext
	 * 
	 * @param command
	 * @param map
	 */
	void modifyContext(CommandElement command, CommandContext context);
}
