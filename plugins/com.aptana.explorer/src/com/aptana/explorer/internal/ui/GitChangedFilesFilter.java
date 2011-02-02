/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.internal.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;

class GitChangedFilesFilter extends ViewerFilter
{

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		IResource resource = null;
		if (element instanceof IResource)
		{
			resource = (IResource) element;
		}
		else if (element instanceof IAdaptable)
		{
			IAdaptable adaptable = (IAdaptable) element;
			resource = (IResource) adaptable.getAdapter(IResource.class);
		}
		if (resource != null)
		{
			return isChanged(resource);
		}
		return false;
	}

	private boolean isChanged(IResource resource)
	{
		GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
		if (repo == null)
			return false;

		return repo.resourceOrChildHasChanges(resource);
	}

	private IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

}
