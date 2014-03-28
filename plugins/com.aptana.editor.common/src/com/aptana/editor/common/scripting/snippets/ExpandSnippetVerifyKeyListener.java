/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
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
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension;
import org.eclipse.ui.texteditor.ITextEditorExtension2;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.TriggerType;
import com.aptana.scripting.model.filters.AndFilter;
import com.aptana.scripting.model.filters.HasTriggerFilter;
import com.aptana.scripting.model.filters.ScopeFilter;

/**
 * @author cwilliams
 */
public class ExpandSnippetVerifyKeyListener implements VerifyKeyListener
{

	private final ITextEditor textEditor;
	private final ITextViewer textViewer;
	private final IDocument document;
	private final boolean canModifyEditor;
	private final IContentAssistant contentAssistant;
	private boolean enableSnippetProposals = true;

	public ExpandSnippetVerifyKeyListener(ITextEditor textEditor, ITextViewer viewer, IContentAssistant contentAssistant)
	{
		this.textEditor = textEditor;
		this.canModifyEditor = canModifyEditor(textEditor); // Can we cache this value?
		this.textViewer = viewer;
		this.contentAssistant = contentAssistant;
		document = (textEditor != null) ? textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput())
				: null;
	}

	public void verifyKey(VerifyEvent event)
	{
		if (textViewer == null || document == null || contentAssistant == null || !canModifyEditor || !event.doit
				|| event.character != '\t' || !enableSnippetProposals)
		{
			return;
		}

		// If the editor is linked editing mode - let it do the TAB key processing
		if (LinkedModeModel.hasInstalledModel(document))
		{
			return;
		}
		ITextSelection selection = getSelection();
		if (selection.getLength() == 0)
		{
			try
			{
				char previousChar = document.getChar(selection.getOffset() - 1);
				if (!Character.isWhitespace(previousChar))
				{
					int caretOffset = textViewer.getTextWidget().getCaretOffset();
					List<CommandElement> commandsFromScope = getSnippetsInScope(caretOffset);
					if (!CollectionsUtil.isEmpty(commandsFromScope))
					{
						// chop off portions of prefix from beginning until we have a match!
						String prefix = SnippetsCompletionProcessor.extractPrefixFromDocument(document, caretOffset);
						while (prefix != null && prefix.length() > 0)
						{
							if (hasMatchingSnippet(prefix, commandsFromScope))
							{
								contentAssistant.showPossibleCompletions();
								event.doit = false;
								return;
							}
							prefix = SnippetsCompletionProcessor.narrowPrefix(prefix);
						}
					}
				}
			}
			catch (BadLocationException e)
			{
				// ignore
				return;
			}
		}
	}

	protected List<CommandElement> getSnippetsInScope(int caretOffset)
	{
		String scope = getScope(textViewer, caretOffset);
		AndFilter filter = new AndFilter(new ScopeFilter(scope), new HasTriggerFilter());
		return getBundleManager().getExecutableCommands(filter);
	}

	private ITextSelection getSelection()
	{
		if (textEditor == null || textEditor.getSelectionProvider() == null)
		{
			return TextSelection.emptySelection();
		}
		return (ITextSelection) textEditor.getSelectionProvider().getSelection();
	}

	private BundleManager getBundleManager()
	{
		return BundleManager.getInstance();
	}

	private boolean hasMatchingSnippet(String prefix, List<CommandElement> commandsFromScope)
	{
		for (CommandElement commandElement : commandsFromScope)
		{
			String[] triggers = commandElement.getTriggerTypeValues(TriggerType.PREFIX);
			if (!ArrayUtil.isEmpty(triggers))
			{
				for (String trigger : triggers)
				{
					if (ObjectUtil.areEqual(prefix, trigger))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Set the key listener as enabled or disabled
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled)
	{
		enableSnippetProposals = enabled;
	}

	/**
	 * Is the key listener currently enabled or disabled?
	 * 
	 * @param enabled
	 */
	public boolean isEnabled()
	{
		return enableSnippetProposals;
	}

	private boolean canModifyEditor(ITextEditor editor)
	{
		if (editor instanceof ITextEditorExtension2)
		{
			return ((ITextEditorExtension2) editor).isEditorInputModifiable();
		}
		else if (editor instanceof ITextEditorExtension)
		{
			return !((ITextEditorExtension) editor).isEditorInputReadOnly();
		}
		else if (editor != null)
		{
			return editor.isEditable();
		}
		return false;
	}

	private String getScope(ITextViewer viewer, int offset)
	{
		String scope = StringUtil.EMPTY;
		try
		{
			scope = getDocumentScopeManager().getScopeAtOffset(viewer, offset);
		}
		catch (BadLocationException e)
		{
			// ignore
		}
		return scope;
	}

	private IDocumentScopeManager getDocumentScopeManager()
	{
		return CommonEditorPlugin.getDefault().getDocumentScopeManager();
	}

}
