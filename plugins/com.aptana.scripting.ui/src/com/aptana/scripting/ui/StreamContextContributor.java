package com.aptana.scripting.ui;

import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.ContextContributor;

public class StreamContextContributor implements ContextContributor
{
	public StreamContextContributor()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.ContextContributor#modifyContext(com.aptana.scripting.model.CommandElement,
	 * com.aptana.scripting.model.CommandContext)
	 */
	public void modifyContext(CommandElement command, CommandContext context)
	{
		context.setConsoleStream(ScriptingConsole.getInstance().getOutputConsoleStream());
		context.setErrorStream(ScriptingConsole.getInstance().getErrorConsoleStream());
	}
}
