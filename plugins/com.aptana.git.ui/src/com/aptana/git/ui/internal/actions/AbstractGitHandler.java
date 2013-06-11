/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.ui.util.UIUtils;

public abstract class AbstractGitHandler extends AbstractHandler
{
	private IEvaluationContext evalContext;
	private boolean enabled;

	private Collection<IResource> selectedResources;

	public void setSelectedResources(Collection<IResource> resources)
	{
		selectedResources = resources;
	}

	protected void openError(final String title, final String msg)
	{
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				MessageDialog.openError(getShell(), Messages.CommitAction_MultipleRepos_Title,
						Messages.CommitAction_MultipleRepos_Message);
			}
		});
	}

	protected Shell getShell()
	{
		return UIUtils.getActiveShell();
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		this.evalContext = (IEvaluationContext) evaluationContext;
		try
		{
			this.enabled = calculateEnabled();
		}
		finally
		{
			this.evalContext = null;
		}
	}

	/**
	 * Subclasses should override to determine if the handler is enabled given the evaluation context!
	 * 
	 * @return
	 */
	protected boolean calculateEnabled()
	{
		return true;
	}

	@Override
	public boolean isEnabled()
	{
		return this.enabled;
	}

	protected IWorkbenchPage getActivePage()
	{
		return UIUtils.getActivePage();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		this.evalContext = (IEvaluationContext) event.getApplicationContext();
		try
		{
			return doExecute(event);
		}
		finally
		{
			this.evalContext = null;
		}
	}

	protected abstract Object doExecute(ExecutionEvent event) throws ExecutionException;

	protected Set<GitRepository> getSelectedRepositories()
	{
		Collection<IResource> resources = getSelectedResources();
		if (resources == null || resources.isEmpty())
		{
			return Collections.emptySet();
		}
		Set<GitRepository> repos = new HashSet<GitRepository>();
		for (IResource resource : resources)
		{
			if (resource == null)
			{
				continue;
			}
			IProject project = resource.getProject();
			GitRepository repo = getGitRepositoryManager().getAttached(project);
			if (repo != null)
			{
				repos.add(repo);
			}
		}
		return repos;
	}

	protected IEditorPart getEditor()
	{
		IWorkbenchPart activePart = (IWorkbenchPart) evalContext.getVariable(ISources.ACTIVE_PART_NAME);
		if (activePart instanceof IEditorPart)
		{
			return (IEditorPart) activePart;
		}
		return null;
	}

	protected Collection<IResource> getSelectedResources()
	{
		if (selectedResources != null)
		{
			return selectedResources;
		}

		Collection<IResource> resources = new ArrayList<IResource>();
		Object activePart = evalContext.getVariable(ISources.ACTIVE_PART_NAME);
		if (activePart instanceof IEditorPart)
		{
			IEditorInput input = (IEditorInput) evalContext.getVariable(ISources.ACTIVE_EDITOR_INPUT_NAME);
			resources.add((IResource) input.getAdapter(IResource.class));
		}
		else
		{
			Object selection = evalContext.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
			if (selection instanceof IStructuredSelection)
			{
				IStructuredSelection struct = (IStructuredSelection) selection;
				for (Object firstElement : struct.toList())
				{
					if (firstElement instanceof IResource)
					{
						resources.add((IResource) firstElement);
					}
					else if (firstElement instanceof IAdaptable)
					{
						IAdaptable adaptable = (IAdaptable) firstElement;
						resources.add((IResource) adaptable.getAdapter(IResource.class));
					}
				}
			}
		}
		return resources;
	}

	protected static IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	protected GitRepository getSelectedRepository()
	{
		Set<GitRepository> repos = getSelectedRepositories();
		if (repos == null || repos.isEmpty())
		{
			return null;
		}
		return repos.iterator().next();
	}
}
