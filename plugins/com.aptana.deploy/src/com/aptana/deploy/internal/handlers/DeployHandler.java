/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.progress.UIJob;

import com.aptana.deploy.IDeployProvider;
import com.aptana.deploy.internal.DeployProviderRegistry;
import com.aptana.deploy.preferences.DeployPreferenceUtil;

public class DeployHandler extends AbstractHandler
{

	private IProject selectedProject;

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		final DeployProviderRegistry registry = DeployProviderRegistry.getInstance();
		final IDeployProvider provider = registry.getProvider(selectedProject);

		// TODO What if provider is still null? Prompt to choose explicitly? Run wizard?
		if (provider != null)
		{
			// Run in a job
			Job job = new UIJob(Messages.DeployHandler_DeployJobTitle)
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					provider.deploy(selectedProject, monitor);
					// Store the deployment provider explicitly, since we may have had none explicitly set, but detected
					// one that works.
					DeployPreferenceUtil.setDeployType(selectedProject, registry.getIdForProvider(provider));
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.setPriority(Job.SHORT);
			job.schedule();
		}
		return null;
	}

	@Override
	public boolean isEnabled()
	{
		return selectedProject != null && selectedProject.isAccessible();
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		selectedProject = null;
		if (evaluationContext instanceof EvaluationContext)
		{
			Object value = ((EvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
			if (value instanceof ISelection)
			{
				ISelection selections = (ISelection) value;
				if (!selections.isEmpty() && selections instanceof IStructuredSelection)
				{
					Object selection = ((IStructuredSelection) selections).getFirstElement();
					IResource resource = null;
					if (selection instanceof IResource)
					{
						resource = (IResource) selection;
					}
					else if (selection instanceof IAdaptable)
					{
						resource = (IResource) ((IAdaptable) selection).getAdapter(IResource.class);
					}
					if (resource != null)
					{
						selectedProject = resource.getProject();
					}
				}
			}
		}
	}
}
