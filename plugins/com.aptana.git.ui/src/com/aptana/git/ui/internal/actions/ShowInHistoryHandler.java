/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.history.IHistoryView;
import org.eclipse.ui.IFileEditorInput;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;

public class ShowInHistoryHandler extends AbstractHandler
{

	private boolean enabled;

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		if (evaluationContext instanceof EvaluationContext)
		{
			IResource resource = getResource((EvaluationContext) evaluationContext);
			if (resource != null)
			{
				GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
				if (repo != null)
				{
					enabled = true;
					return;
				}
			}
		}
		enabled = false;
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (event == null)
		{
			return null;
		}
		Object context = event.getApplicationContext();
		if (context instanceof EvaluationContext)
		{
			IResource resource = getResource((EvaluationContext) context);
			if (resource != null)
			{
				IHistoryView view = TeamUI.getHistoryView();
				if (view != null)
				{
					view.showHistoryFor(resource);
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private IResource getResource(EvaluationContext evContext)
	{
		Object input = evContext.getVariable("showInInput"); //$NON-NLS-1$
		if (input instanceof IFileEditorInput)
		{
			IFileEditorInput fei = (IFileEditorInput) input;
			return fei.getFile();
		}

		Collection<Object> selectedFiles = (Collection<Object>) evContext.getDefaultVariable();
		for (Object selected : selectedFiles)
		{
			if (selected instanceof IResource)
			{
				return (IResource) selected;
			}
			else if (selected instanceof IAdaptable)
			{
				return (IResource) ((IAdaptable) selected).getAdapter(IResource.class);
			}

		}
		return null;
	}

}
