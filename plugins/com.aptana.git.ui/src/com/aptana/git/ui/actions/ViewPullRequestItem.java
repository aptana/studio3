/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.actions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IEvaluationService;
import org.eclipse.ui.services.IServiceLocator;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubPullRequest;
import com.aptana.git.core.github.IGithubRepository;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.ui.internal.actions.AbstractGithubHandler;

public class ViewPullRequestItem extends CompoundContributionItem implements IWorkbenchContribution
{

	protected static final IContributionItem[] NO_CONTRIBUTION_ITEMS = new IContributionItem[0];

	private IServiceLocator serviceLocator;

	public ViewPullRequestItem()
	{
		super();
	}

	public ViewPullRequestItem(String id)
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

	@Override
	protected IContributionItem[] getContributionItems()
	{
		IResource resource = getSelectedResource();
		if (resource == null)
		{
			return NO_CONTRIBUTION_ITEMS;
		}

		final GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
		if (repo == null)
		{
			return NO_CONTRIBUTION_ITEMS;
		}

		try
		{
			IGithubRepository ghRepo = repo.getGithubRepo();
			List<IGithubPullRequest> prs = ghRepo.getOpenPullRequests();

			Collection<IContributionItem> contributions = new ArrayList<IContributionItem>(prs.size());
			for (final IGithubPullRequest pr : prs)
			{
				contributions.add(new ViewPullRequestContributionItem(pr));
			}
			return contributions.toArray(new IContributionItem[contributions.size()]);
		}
		catch (CoreException e)
		{
			return NO_CONTRIBUTION_ITEMS;
		}
	}

	private static class ViewPullRequestContributionItem extends ContributionItem
	{

		private IGithubPullRequest pr;

		ViewPullRequestContributionItem(IGithubPullRequest pr)
		{
			this.pr = pr;
		}

		@Override
		public void fill(Menu menu, int index)
		{
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index++);
			menuItem.setText(MessageFormat.format("#{0} - {1}", Long.toString(pr.getNumber()), pr.getTitle()));
			menuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					// what to do when menu is subsequently selected.
					AbstractGithubHandler.viewPullRequest(pr);
				}
			});
		}
	}
}
