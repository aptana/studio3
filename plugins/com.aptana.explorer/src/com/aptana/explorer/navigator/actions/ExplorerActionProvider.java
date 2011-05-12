/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.navigator.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import com.aptana.explorer.IExplorerUIConstants;
import com.aptana.ui.actions.DefaultNavigatorActionProvider;

abstract public class ExplorerActionProvider extends DefaultNavigatorActionProvider
{

	@Override
	protected boolean isEnabled()
	{
		ICommonViewerSite site = getActionSite().getViewSite();
		if (site instanceof ICommonViewerWorkbenchSite)
		{
			IWorkbenchPartSite partSite = ((ICommonViewerWorkbenchSite) site).getSite();
			String siteId = partSite.getId();
			if (IExplorerUIConstants.VIEW_ID.equals(siteId) || IPageLayout.ID_PROJECT_EXPLORER.equals(siteId))
			{
				IProject project = getSelectedProject();
				return project != null && project.isAccessible();
			}
		}
		return super.isEnabled();
	}

	protected IProject getSelectedProject()
	{
		ISelection selection = getActionSite().getViewSite().getSelectionProvider().getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty())
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IResource)
			{
				return ((IResource) element).getProject();
			}
			if (element instanceof IAdaptable)
			{
				IResource resource = (IResource) ((IAdaptable) element).getAdapter(IResource.class);
				if (resource != null)
				{
					return resource.getProject();
				}
			}
		}
		return null;
	}
}
