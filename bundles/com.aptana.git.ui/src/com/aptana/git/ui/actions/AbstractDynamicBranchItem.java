/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.actions;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IEvaluationService;
import org.eclipse.ui.services.IServiceLocator;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.IGitRepositoryManager;

abstract class AbstractDynamicBranchItem extends CompoundContributionItem implements IWorkbenchContribution
{

	protected static final IContributionItem[] NO_CONTRIBUTION_ITEMS = new IContributionItem[0];

	private IServiceLocator serviceLocator;

	protected AbstractDynamicBranchItem()
	{
		super();
	}

	protected AbstractDynamicBranchItem(String id)
	{
		super(id);
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	protected IResource getSelectedResource()
	{
		IEvaluationService evalService = (IEvaluationService) serviceLocator.getService(IEvaluationService.class);

		if (evalService != null)
		{
			IEvaluationContext context = evalService.getCurrentState();
			IWorkbenchPart activePart = (IWorkbenchPart) context.getVariable(ISources.ACTIVE_PART_NAME);
			if (activePart instanceof IEditorPart)
			{
				IEditorInput input = (IEditorInput) context.getVariable(ISources.ACTIVE_EDITOR_INPUT_NAME);
				return (IResource) input.getAdapter(IResource.class);
			}
			ISelection selection = (ISelection) context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
			if (selection instanceof IStructuredSelection)
			{
				IStructuredSelection struct = (IStructuredSelection) selection;
				Object firstElement = struct.getFirstElement();
				if (firstElement instanceof IResource)
				{
					return (IResource) firstElement;
				}
				else if (firstElement instanceof IAdaptable)
				{
					IAdaptable adaptable = (IAdaptable) firstElement;
					return (IResource) adaptable.getAdapter(IResource.class);
				}
			}
		}
		return null;
	}

	public void initialize(IServiceLocator serviceLocator)
	{
		this.serviceLocator = serviceLocator;
	}
}
