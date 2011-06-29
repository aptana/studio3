/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RegistryToggleState;

import com.aptana.editor.common.AbstractThemeableEditor;

public class ToggleWordWrapHandler extends AbstractHandler
{

	private static final String COMMAND_ID = "com.aptana.editor.toggleWordWrapCommand"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		// this applies to the specific editor, so no need to modify the global preference
		IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
		if (editorPart instanceof AbstractThemeableEditor)
		{
			AbstractThemeableEditor activeEditor = (AbstractThemeableEditor) editorPart;
			activeEditor.setWordWrapEnabled(!activeEditor.getWordWrapEnabled());
		}
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		Object activeSite = ((IEvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_SITE_NAME);
		Object activeEditor = ((IEvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_EDITOR_NAME);
		if (activeSite instanceof IWorkbenchSite && activeEditor instanceof AbstractThemeableEditor)
		{
			ICommandService commandService = (ICommandService) ((IWorkbenchSite) activeSite)
					.getService(ICommandService.class);
			Command command = commandService.getCommand(COMMAND_ID);
			State state = command.getState(RegistryToggleState.STATE_ID);
			state.setValue(((AbstractThemeableEditor) activeEditor).getWordWrapEnabled());
		}
	}
}
