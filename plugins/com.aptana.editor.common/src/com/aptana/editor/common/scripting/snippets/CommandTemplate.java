/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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

	CommandTemplate(CommandElement commandElement, String trigger, String contextTypeId)
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

	@Override
	public String toString()
	{
		return getName() + " - " + getPattern(); //$NON-NLS-1$
	}

}