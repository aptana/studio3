package com.aptana.git.ui.internal.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.actions.GitAction;

public abstract class ChangedFileAction extends GitAction
{
	protected ChangedFile getChangedFile(IResource resource)
	{
		if (!(resource instanceof IFile))
			return null;
		GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
		if (repo == null)
			return null;
		return getChangedFile(repo, resource);
	}

	private ChangedFile getChangedFile(GitRepository repo, IResource resource)
	{
		return repo.getChangedFileForResource(resource);
	}
}
