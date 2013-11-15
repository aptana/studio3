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
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPage;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.ui.internal.history.GitHistoryPageSource;
import com.aptana.ui.util.UIUtils;

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
		if (evaluationContext instanceof IEvaluationContext)
		{
			IResource resource = getResource((IEvaluationContext) evaluationContext);
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
		if (context instanceof IEvaluationContext)
		{
			IResource resource = getResource((IEvaluationContext) context);
			if (resource != null)
			{
				IWorkbenchPage page = UIUtils.getActivePage();
				TeamUI.showHistoryFor(page, resource, GitHistoryPageSource.getInstance());
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private IResource getResource(IEvaluationContext evContext)
	{
		// If we have showIn value, use it
		Object input = evContext.getVariable(ISources.SHOW_IN_INPUT);
		if (input instanceof IFileEditorInput)
		{
			IFileEditorInput fei = (IFileEditorInput) input;
			return fei.getFile();
		}

		// Otherwise check if we are inside an editor
		Object selection = evContext.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
		if (selection instanceof ITextSelection)
		{
			// we're inside an editor!
			input = evContext.getVariable(ISources.ACTIVE_EDITOR_INPUT_NAME);
			if (input instanceof IFileEditorInput)
			{
				IFileEditorInput fei = (IFileEditorInput) input;
				return fei.getFile();
			}
		}

		// Try the default variable, probably a selection in Project/App Explorer
		input = evContext.getDefaultVariable();
		if (input instanceof IStructuredSelection)
		{
			IStructuredSelection ss = (IStructuredSelection) input;
			Object[] selectedFiles = ss.toArray();
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
		}
		else if (input instanceof Collection)
		{
			Collection<Object> selectedFiles = (Collection<Object>) input;
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
		}
		return null;
	}

}
