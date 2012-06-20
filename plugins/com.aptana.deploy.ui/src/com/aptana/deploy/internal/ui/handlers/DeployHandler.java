/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISources;
import org.eclipse.ui.progress.UIJob;

import com.aptana.deploy.IDeployProvider;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.util.DeployProviderUtil;

public class DeployHandler extends AbstractHandler
{

	private IContainer selectedContainer;

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		final IContainer container = selectedContainer;
		final IDeployProvider provider = DeployProviderUtil.getDeployProvider(selectedContainer);

		// TODO What if provider is still null? Prompt to choose explicitly? Run wizard?
		if (provider != null)
		{
			// Run in a job
			Job job = new UIJob(Messages.DeployHandler_DeployJobTitle)
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					provider.deploy(container, monitor);
					// Store the deployment provider explicitly, since we may have had none explicitly set, but detected
					// one that works.
					DeployPreferenceUtil.setDeployType(container, DeployProviderUtil.getIdForProvider(provider));
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
		return selectedContainer != null && selectedContainer.isAccessible();
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		selectedContainer = null;
		if (evaluationContext instanceof IEvaluationContext)
		{
			Object activePart = ((IEvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_PART_NAME);
			if (activePart instanceof IEditorPart)
			{
				IEditorInput editorInput = ((IEditorPart) activePart).getEditorInput();
				if (editorInput instanceof IFileEditorInput)
				{
					// uses the parent folder
					selectedContainer = ((IFileEditorInput) editorInput).getFile().getParent();
				}
			}
			else
			{
				Object value = ((IEvaluationContext) evaluationContext)
						.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
				if (value instanceof ISelection)
				{
					ISelection selections = (ISelection) value;
					if (!selections.isEmpty() && selections instanceof IStructuredSelection)
					{
						Object selection = ((IStructuredSelection) selections).getFirstElement();
						if (selection instanceof IContainer)
						{
							selectedContainer = (IContainer) selection;
						}
						else if (selection instanceof IAdaptable)
						{
							IResource resource = (IResource) ((IAdaptable) selection).getAdapter(IResource.class);
							if (resource != null)
							{
								selectedContainer = resource.getParent();
							}
						}
					}
				}
			}
		}
	}
}
