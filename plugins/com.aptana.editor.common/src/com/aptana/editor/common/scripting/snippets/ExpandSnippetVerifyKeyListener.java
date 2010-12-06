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

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension;
import org.eclipse.ui.texteditor.ITextEditorExtension2;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.TriggerType;
import com.aptana.scripting.model.filters.AndFilter;
import com.aptana.scripting.model.filters.HasTriggerFilter;
import com.aptana.scripting.model.filters.ScopeFilter;

public class ExpandSnippetVerifyKeyListener implements VerifyKeyListener
{

	private final ITextEditor textEditor;
	private ITextViewer textViewer;
	private IDocument document;
	private ContentAssistant contentAssistant;
	private boolean canModifyEditor;

	public ExpandSnippetVerifyKeyListener(ITextEditor textEditor, ITextViewer viewer)
	{
		this.textEditor = textEditor;
		this.canModifyEditor = canModifyEditor(textEditor); // Can we cache this value?
		this.textViewer = viewer;

		document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());

		contentAssistant = new SnippetsContentAssistant();
		if (textViewer != null)
		{
			contentAssistant.install(textViewer);
		}
	}

	public void verifyKey(VerifyEvent event)
	{
		if (textViewer == null || document == null)
		{
			return;
		}

		// If the editor is linked editing mode - let it do the TAB key processing
		if (LinkedModeModel.hasInstalledModel(document))
		{
			return;
		}

		if (canModifyEditor && event.doit && event.character == '\t')
		{
			ITextSelection selection = (ITextSelection) textEditor.getSelectionProvider().getSelection();
			if (selection.getLength() == 0)
			{
				int offset = selection.getOffset() - 1;
				try
				{
					String previousChar = document.get(offset, 1);
					if (!Character.isWhitespace(previousChar.charAt(0)))
					{
						int caretOffset = textViewer.getTextWidget().getCaretOffset();
						String scope = getScope(textViewer, caretOffset);
						AndFilter filter = new AndFilter(new ScopeFilter(scope), new HasTriggerFilter());
						List<CommandElement> commandsFromScope = BundleManager.getInstance().getExecutableCommands(filter);
						if (commandsFromScope.size() > 0)
						{
							// chop off portions of prefix from beginning until we have a match!
							String prefix = SnippetsCompletionProcessor
									.extractPrefixFromDocument(document, caretOffset);
							while (prefix != null && prefix.length() > 0)
							{
								if (hasMatchingSnippet(prefix, commandsFromScope))
								{
									if (contentAssistant != null)
									{
										contentAssistant.showPossibleCompletions();
										event.doit = false;
									}
									return;
								}
								prefix = SnippetsCompletionProcessor.narrowPrefix(prefix);
							}
						}
					}
				}
				catch (BadLocationException e)
				{
					return;
				}
			}
		}
	}

	protected boolean hasMatchingSnippet(String prefix, List<CommandElement> commandsFromScope)
	{
		for (CommandElement commandElement : commandsFromScope)
		{
			String[] triggers = commandElement.getTriggerTypeValues(TriggerType.PREFIX);
			if (triggers != null)
			{
				for (String trigger : triggers)
				{
					if (trigger != null && trigger.startsWith(prefix))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	private static boolean canModifyEditor(ITextEditor editor)
	{
		if (editor instanceof ITextEditorExtension2)
			return ((ITextEditorExtension2) editor).isEditorInputModifiable();
		else if (editor instanceof ITextEditorExtension)
			return !((ITextEditorExtension) editor).isEditorInputReadOnly();
		else if (editor != null)
			return editor.isEditable();
		else
			return false;
	}

	private static String getScope(ITextViewer viewer, int offset)
	{
		String scope = ""; //$NON-NLS-1$
		try
		{
			scope = getDocumentScopeManager().getScopeAtOffset(viewer, offset);
		}
		catch (BadLocationException e)
		{
			// TODO
		}
		return scope;
	}

	protected static IDocumentScopeManager getDocumentScopeManager()
	{
		return CommonEditorPlugin.getDefault().getDocumentScopeManager();
	}
}
