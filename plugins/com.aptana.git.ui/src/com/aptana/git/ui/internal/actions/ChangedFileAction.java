package com.aptana.ide.red.git.ui.internal.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.team.internal.ui.actions.TeamAction;

import com.aptana.ide.red.git.model.ChangedFile;
import com.aptana.ide.red.git.model.GitRepository;

abstract class ChangedFileAction extends TeamAction
{

	protected ChangedFile getChangedFile(IResource resource)
	{
		if (!(resource instanceof IFile))
			return null;
		GitRepository repo = GitRepository.instance(resource.getProject());
		if (repo == null)
			return null;
		return getChangedFile(repo, resource);
	}

	private ChangedFile getChangedFile(GitRepository repo, IResource resource)
	{
		return repo.getChangedFileForResource(resource);
	}
}
