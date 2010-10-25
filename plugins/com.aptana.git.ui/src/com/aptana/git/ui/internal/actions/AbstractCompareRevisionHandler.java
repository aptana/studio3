package com.aptana.git.ui.internal.actions;

import java.util.Collection;

import org.eclipse.core.resources.IResource;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;

abstract class AbstractCompareRevisionHandler extends AbstractGitHandler
{

	protected boolean calculateEnabled()
	{
		Collection<IResource> resources = getSelectedResources();
		if (resources == null || resources.size() != 1)
		{
			return false;
		}
		IResource blah = resources.iterator().next();
		if (blah.getType() != IResource.FILE)
		{
			return false;
		}
		GitRepository repo = getGitRepositoryManager().getAttached(blah.getProject());
		if (repo == null)
		{
			return false;
		}
		ChangedFile file = repo.getChangedFileForResource(blah);
		if (file == null)
		{
			return false;
		}
		return file.hasStagedChanges() || file.hasUnstagedChanges() || file.hasUnmergedChanges();
	}

}
