package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.templates.Template;

import com.aptana.scripting.model.CommandElement;

public class CommandTemplate extends Template
{
	private final CommandElement commandElement;

	protected CommandTemplate(CommandElement command, String name, String description, String contextTypeId,
			String pattern, boolean isAutoInsertable)
	{
		super(name, description, contextTypeId, pattern, isAutoInsertable);
		this.commandElement = command;
	}

	public CommandTemplate(CommandElement commandElement, String trigger, String contextTypeId)
	{
		this(commandElement, trigger, commandElement.getDisplayName(), contextTypeId, commandElement.getDisplayName(),
				true);
	}

	public CommandElement getCommandElement()
	{
		return commandElement;
	}

	@Override
	public boolean matches(String prefix, String contextTypeId)
	{
		if (!super.matches(prefix, contextTypeId))
		{
			return false;
		}

		while (prefix != null && prefix.length() > 0)
		{
			if (matches(prefix))
			{
				return true;
			}
			prefix = SnippetsCompletionProcessor.narrowPrefix(prefix);
		}
		return false;
	}

	protected boolean matches(String prefix)
	{
		return getName().toLowerCase().startsWith(prefix.toLowerCase());
	}

	boolean exactMatches(String prefix)
	{
		return prefix != null && prefix.length() != 0 && getName().equalsIgnoreCase(prefix);
	}

	@Override
	public String toString()
	{
		return getName() + " - " + getPattern(); //$NON-NLS-1$
	}

}