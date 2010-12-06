/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
