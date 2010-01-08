package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.scripting.Activator;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.InvocationType;

public class CommandProposal extends SnippetTemplateProposal
{

	public CommandProposal(Template template, TemplateContext context, IRegion region, Image image, int relevance)
	{
		super(template, context, region, image, relevance);
	}

	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset)
	{
		if (contains(triggerChars, trigger))
		{
			if (triggerChar != trigger)
			{
				((ICompletionProposalExtension2) templateProposals[trigger - '1']).apply(viewer, trigger, stateMask,
						offset);
				return;
			}
		}

		Template template = getTemplate();
		if (template instanceof CommandTemplate)
		{
			CommandTemplate commandTemplate = (CommandTemplate) template;
			CommandElement command = commandTemplate.getCommand();
			// Wipe the prefix before we run command so our input is as we expect
			try
			{
				IDocument document = viewer.getDocument();
				int start = getReplaceOffset();
				int end = getReplaceEndOffset();
				String replacement = ""; //$NON-NLS-1$
				if (end == document.getLength())
				{
					String[] lineDelimeters = document.getLegalLineDelimiters();
					if (lineDelimeters != null && lineDelimeters.length > 0)
					{
						replacement = lineDelimeters[0];
					}
					else
					{
						replacement = "\n"; //$NON-NLS-1$
					}
				}
				document.replace(start, end - start, replacement);
			}
			catch (BadLocationException e)
			{
				Activator.logError(e.getMessage(), e);
			}

			ITextEditor textEditor = null;
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null)
			{
				IWorkbenchPage page = window.getActivePage();
				if (page != null)
				{
					IEditorPart part = page.getActiveEditor();
					if (part instanceof ITextEditor)
					{
						textEditor = (ITextEditor) part;
					}
				}
			}
			CommandResult commandResult = CommandExecutionUtils.executeCommand(command, InvocationType.TRIGGER, viewer, textEditor);
			CommandExecutionUtils.processCommandResult(command, commandResult, viewer);
		}
	}

}
