/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.scripting;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.editor.common.scripting.commands.TextEditorUtils;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.InvocationType;
import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.model.filters.IModelFilter;
import com.aptana.scripting.model.filters.ScopeFilter;
import com.aptana.scripting.ui.ICommandElementsProvider;
import com.aptana.scripting.ui.KeyBindingUtil;

public class CommandElementsProvider implements ICommandElementsProvider
{

	private final ITextEditor textEditor;
	private final ITextViewer textViewer;

	public CommandElementsProvider(ITextEditor textEditor, ITextViewer textViewer)
	{
		this.textEditor = textEditor;
		this.textViewer = textViewer;
	}

	public void execute(CommandElement commandElement)
	{
		CommandResult commandResult = CommandExecutionUtils.executeCommand(commandElement, InvocationType.KEY_BINDING,
				textViewer, textEditor);
		CommandExecutionUtils.processCommandResult(commandElement, commandResult, textEditor);
	}

	public List<CommandElement> getCommandElements(KeySequence keySequence)
	{
		List<CommandElement> commandElements = new LinkedList<CommandElement>();
		int caretOffset = TextEditorUtils.getCaretOffset(textEditor);

		try
		{
			String contentTypeAtOffset = CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getScopeAtOffset(textViewer, caretOffset);
			IModelFilter filter = new ScopeFilter(contentTypeAtOffset);

			List<CommandElement> commandsFromScope = BundleManager.getInstance().getExecutableCommands(filter);
			for (CommandElement commandElement : commandsFromScope)
			{
				if (commandElement instanceof SnippetElement)
				{
					continue;
				}
				KeySequence[] commandElementKeySequences = KeyBindingUtil.getKeySequences(commandElement);
				for (KeySequence commandElementKeySequence : commandElementKeySequences)
				{
					if (keySequence.equals(commandElementKeySequence))
					{
						commandElements.add(commandElement);
						break;
					}
				}
			}
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
		return commandElements;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.keybindings.ICommandElementsProvider#getCommandElementsPopupLocation()
	 */
	public Point getCommandElementsPopupLocation()
	{
		Object control = textEditor.getAdapter(Control.class);
		if (control instanceof StyledText)
		{
			StyledText textWidget = (StyledText) control;
			int caretOffset = textWidget.getCaretOffset();
			Point locationAtOffset = textWidget.getLocationAtOffset(caretOffset);
			locationAtOffset = textWidget.toDisplay(locationAtOffset.x,
					locationAtOffset.y + textWidget.getLineHeight(caretOffset) + 2);
			return locationAtOffset;
		}
		return null;
	}
}
