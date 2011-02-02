/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.terminal.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.ISources;

import com.aptana.terminal.views.TerminalView;

public class OpenTerminalHandler extends AbstractHandler
{

	private IProject fProject;

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (fProject != null)
		{
			TerminalView.openView(fProject.getName(), fProject.getName(), fProject.getLocation());
		}
		return null;
	}

	@Override
	public boolean isEnabled()
	{
		return fProject != null && fProject.exists();
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		fProject = null;
		if (evaluationContext instanceof EvaluationContext)
		{
			Object activePart = ((EvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_PART_NAME);
			if (activePart instanceof IAdaptable)
			{
				fProject = (IProject) ((IAdaptable) activePart).getAdapter(IProject.class);
			}
		}
	}
}
