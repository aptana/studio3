package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.templates.Template;

import com.aptana.scripting.model.CommandElement;

public class CommandTemplate extends Template {

	private final CommandElement command;

	public CommandTemplate(CommandElement command, String contextTypeId) {
		super(command.getTrigger(),
				command.getDisplayName(), 
				contextTypeId,
				command.getInvoke(),
				true);
		this.command = command;
	}
	
	public CommandElement getCommand() {
		return command;
	}

}
