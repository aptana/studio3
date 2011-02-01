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
		if (resources == null || resources.isEmpty())
		{
			return false;
		}
		for (IResource blah : resources)
		{
			if (blah == null || blah.getType() != IResource.FILE)
			{
				continue;
			}
			GitRepository repo = getGitRepositoryManager().getAttached(blah.getProject());
			if (repo == null)
			{
				continue;
			}
			ChangedFile file = repo.getChangedFileForResource(blah);
			if (file == null)
			{
				continue;
			}
			if (file.hasStagedChanges() || file.hasUnstagedChanges() || file.hasUnmergedChanges())
			{
				return true;
			}
		}
		return false;
	}

}
