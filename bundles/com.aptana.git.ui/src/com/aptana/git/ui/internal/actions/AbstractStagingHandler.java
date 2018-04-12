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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;

abstract class AbstractStagingHandler extends AbstractGitHandler
{

	private ChangedFile getChangedFile(IResource resource)
	{
		if (!(resource instanceof IFile))
		{
			return null;
		}
		GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
		if (repo == null)
		{
			return null;
		}
		return getChangedFile(repo, resource);
	}

	private ChangedFile getChangedFile(GitRepository repo, IResource resource)
	{
		return repo.getChangedFileForResource(resource);
	}

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		Map<GitRepository, List<ChangedFile>> repoToChangedFiles = new HashMap<GitRepository, List<ChangedFile>>();
		Collection<IResource> resources = getSelectedResources();
		for (IResource resource : resources)
		{
			if (resource instanceof IContainer)
			{
				IContainer container = (IContainer) resource;
				GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
				List<ChangedFile> files = repo.getChangedFilesForContainer(container);
				// FIXME just filter and add them all at the same time!
				for (ChangedFile file : files)
				{
					if (!changedFileIsValid(file))
					{
						continue;
					}

					List<ChangedFile> changedFiles = repoToChangedFiles.get(repo);
					if (changedFiles == null)
					{
						changedFiles = new ArrayList<ChangedFile>();
						repoToChangedFiles.put(repo, changedFiles);
					}
					changedFiles.add(file);
				}
			}
			else
			{
				ChangedFile correspondingChangedFile = getChangedFile(resource);
				if (!changedFileIsValid(correspondingChangedFile))
				{
					continue;
				}

				GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
				List<ChangedFile> changedFiles = repoToChangedFiles.get(repo);
				if (changedFiles == null)
				{
					changedFiles = new ArrayList<ChangedFile>();
					repoToChangedFiles.put(repo, changedFiles);
				}
				changedFiles.add(correspondingChangedFile);
			}
		}

		for (Map.Entry<GitRepository, List<ChangedFile>> entry : repoToChangedFiles.entrySet())
		{
			doOperation(entry.getKey(), entry.getValue());
		}

		return null;
	}

	protected abstract void doOperation(GitRepository repo, List<ChangedFile> changedFiles);

	@Override
	protected boolean calculateEnabled()
	{
		Collection<IResource> resources = getSelectedResources();
		if (resources.isEmpty())
		{
			return true;
		}
		for (IResource resource : resources)
		{
			if (!isEnabledForResource(resource))
			{
				return false;
			}
		}
		return true;
	}

	private boolean isEnabledForResource(IResource resource)
	{
		if (resource instanceof IContainer)
		{
			IContainer container = (IContainer) resource;
			GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
			if (repo != null)
			{
				List<ChangedFile> files = repo.getChangedFilesForContainer(container);
				for (ChangedFile file : files)
				{
					if (changedFileIsValid(file))
					{
						return true;
					}
				}
			}
		}
		return changedFileIsValid(getChangedFile(resource));
	}

	protected abstract boolean changedFileIsValid(ChangedFile correspondingChangedFile);

}
