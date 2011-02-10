/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
		if (textViewer == null || document == null || !canModifyEditor || !event.doit || event.character != '\t')
		{
			return;
		}

		// If the editor is linked editing mode - let it do the TAB key processing
		if (LinkedModeModel.hasInstalledModel(document))
		{
			return;
		}

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
						String prefix = SnippetsCompletionProcessor.extractPrefixFromDocument(document, caretOffset);
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
