/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.editor.common.scripting.commands.FilterThroughCommandDialog;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.InvocationType;

public class FilterThroughCommandAction extends TextEditorAction
{

	public static IAction create(ITextEditor textEditor)
	{
		return new FilterThroughCommandAction(ResourceBundle.getBundle(FilterThroughCommandAction.class.getName()),
				"FilterThroughCommandAction.", textEditor); //$NON-NLS-1$
	}

	public static final String COMMAND_ID = "com.aptana.editor.common.scripting.commands.FilterThroughCommand"; //$NON-NLS-1$

	private boolean deactivated = false;

	private FilterThroughCommandAction(ResourceBundle bundle, String prefix, ITextEditor editor)
	{
		super(bundle, prefix, editor);
		setActionDefinitionId(COMMAND_ID);
	}

	@Override
	public void run()
	{
		ITextEditor textEditor = getTextEditor();

		IWorkbenchWindow workbenchWindow = textEditor.getEditorSite().getWorkbenchWindow();
		// TODO: probably need to grab or generate a ENV map from a Command here
		// Map<String, String> environment = CommandExecutionUtils.computeEnvironment(textEditor);
		Map<String, String> environment = new HashMap<String, String>();

		FilterThroughCommandDialog filterThroughCommandDialog = new FilterThroughCommandDialog(
				workbenchWindow.getShell(), environment);
		if (filterThroughCommandDialog.open() == Window.OK)
		{
			CommandElement command = new CommandElement(null); // Use null value for path to create a one off command
			command.setInputType(filterThroughCommandDialog.getInputType().getName());
			command.setOutputType(filterThroughCommandDialog.getOuputType().getName());
			command.setInvoke(filterThroughCommandDialog.getCommand());
			CommandResult commandResult = CommandExecutionUtils.executeCommand(command, InvocationType.UNKNOWN,
					textEditor);
			CommandExecutionUtils.processCommandResult(command, commandResult, textEditor);
		}
	}

	void adjustHandledState()
	{
		if (isDeactivated())
		{
			deactivate();
			return;
		}
		if (!getTextEditor().isEditable())
		{
			deactivate();
			return;
		}
		activate();
	}

	private boolean isDeactivated()
	{
		return deactivated;
	}

	private void activate()
	{
		getTextEditor().setAction(COMMAND_ID, this);
	}

	private void deactivate()
	{
		getTextEditor().setAction(COMMAND_ID, null);
	}
}
