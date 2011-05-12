/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.util.Collection;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;

public class IgnoreHandler extends AbstractGitHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		Collection<IResource> resources = getSelectedResources();
		for (IResource resource : resources)
		{
			if (resource == null)
			{
				continue;
			}
			GitRepository repo = GitPlugin.getDefault().getGitRepositoryManager().getAttached(resource.getProject());
			repo.ignoreResource(resource);
		}
		return null;
	}

}
