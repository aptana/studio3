package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.templates.Template;

import com.aptana.scripting.model.CommandElement;

public class CommandTemplate extends Template {

	private final CommandElement command;

	public CommandTemplate(CommandElement command, String contextTypeId) {
		super(command.getTriggers(),
				command.getDisplayName(), 
				contextTypeId,
				(command.getInvoke() == null ? command.getDisplayName(): command.getInvoke()),
				true);
		this.command = command;
	}
	
	public CommandElement getCommand() {
		return command;
	}
	
	@Override
	public boolean matches(String prefix, String contextTypeId) {
		boolean matches = super.matches(prefix, contextTypeId);
		if (!matches) {
			return matches;
		}
		return prefix != null && prefix.length() != 0 && getName().toLowerCase().startsWith(prefix.toLowerCase());
	}

}
