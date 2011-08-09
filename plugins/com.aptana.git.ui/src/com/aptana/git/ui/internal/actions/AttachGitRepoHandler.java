/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISources;

import com.aptana.core.logging.IdeLog;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.actions.Messages;

public class AttachGitRepoHandler extends AbstractHandler
{

	private boolean enabled;

	@Override
	public boolean isEnabled()
	{
		return this.enabled;
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		final IProject selectedProject = getSelectedProject(evaluationContext);
		if (selectedProject == null)
		{
			this.enabled = false;
			return;
		}
		GitRepository repo = getGitRepositoryManager().getAttached(selectedProject);
		this.enabled = (repo == null);
	}

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		final IProject selectedProject = getSelectedProject(event.getApplicationContext());
		if (selectedProject == null)
		{
			return null;
		}
		Job job = new Job(Messages.GitProjectView_AttachGitRepo_jobTitle)
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				SubMonitor sub = SubMonitor.convert(monitor, 100);
				try
				{
					getGitRepositoryManager().createOrAttach(selectedProject, sub.newChild(100));
				}
				catch (CoreException e)
				{
					IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
					return e.getStatus();
				}
				finally
				{
					sub.done();
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.LONG);
		job.schedule();
		return null;
	}

	private IProject getSelectedProject(Object applicationContext)
	{
		ISelection sel = getSelection(applicationContext);
		if (sel == null || sel.isEmpty() || !(sel instanceof IStructuredSelection))
		{
			return null;
		}
		IStructuredSelection structured = (IStructuredSelection) sel;
		Object first = structured.getFirstElement();
		IResource resource = null;
		if (first instanceof IResource)
		{
			resource = (IResource) first;
		}
		else if (first instanceof IAdaptable)
		{
			IAdaptable adaptable = (IAdaptable) first;
			resource = (IResource) adaptable.getAdapter(IResource.class);
		}

		if (resource != null)
		{
			return resource.getProject();
		}

		return null;
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	private ISelection getSelection(Object evalContext)
	{
		if (evalContext instanceof IEvaluationContext)
		{
			Object obj = ((IEvaluationContext) evalContext).getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
			if (obj == null)
			{
				return null;
			}
			if (obj instanceof ISelection)
			{
				return (ISelection) obj;
			}
			// TODO Handle list/array/collection!
			return new StructuredSelection(obj);
		}
		return null;
	}
}
