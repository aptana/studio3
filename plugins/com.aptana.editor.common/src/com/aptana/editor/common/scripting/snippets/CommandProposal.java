package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;

public class CommandProposal extends SnippetTemplateProposal {

	public CommandProposal(Template template, TemplateContext context, IRegion region, Image image, int relevance) {
		super(template, context, region, image, relevance);
	}
	
	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		if (contains(triggerChars, trigger)) {
			if (triggerChar != trigger) {
				((ICompletionProposalExtension2)templateProposals[trigger - '1']).apply(viewer, trigger, stateMask, offset);
				return;
			}
		}
		
		Template template = getTemplate();
		if (template instanceof CommandTemplate) {
			CommandTemplate commandTemplate = (CommandTemplate) template;
			CommandElement command = commandTemplate.getCommand();
			CommandResult commandResult = CommandExecutionUtils.executeCommand(command, viewer);
			CommandExecutionUtils.processCommandResult(command, commandResult, viewer);
		}
	}

}
