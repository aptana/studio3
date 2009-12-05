package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.templates.Template;

import com.aptana.scripting.model.Command;

public class CommandTemplate extends Template {

	private final Command command;

	public CommandTemplate(Command command, String contextTypeId) {
		super(command.getTrigger(),
				command.getDisplayName(), 
				contextTypeId,
				command.getInvoke(),
				true);
		this.command = command;
	}
	
	public Command getCommand() {
		return command;
	}

}
