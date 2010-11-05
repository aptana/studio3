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
