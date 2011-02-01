/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.scripting.ScriptingActivator;
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
	protected void doApply(ITextViewer viewer, char trigger, int stateMask, int offset)
	{
		Template template = getTemplate();
		if (template instanceof CommandTemplate)
		{
			CommandTemplate commandTemplate = (CommandTemplate) template;
			CommandElement commandElement = commandTemplate.getCommandElement();
			// Wipe the prefix before we run command so our input is as we expect
			try
			{
				IDocument document = viewer.getDocument();
				int start = getReplaceOffset();
				int end = offset;
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
				ScriptingActivator.logError(e.getMessage(), e);
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
			CommandResult commandResult = CommandExecutionUtils.executeCommand(commandElement, InvocationType.TRIGGER, viewer, textEditor);
			CommandExecutionUtils.processCommandResult(commandElement, commandResult, viewer);
		}
	}

	/*
	 * @see ICompletionProposal#getSelection(IDocument)
	 */
	@Override
	public Point getSelection(IDocument document)
	{
		// Return null for Command Proposal
		return null;
	}

}
