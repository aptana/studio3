package com.aptana.scripting.model;

public interface ContextContributor
{
	/**
	 * modifyContext
	 * 
	 * @param command
	 * @param context
	 */
	void modifyContext(CommandElement command, CommandContext context);
}
