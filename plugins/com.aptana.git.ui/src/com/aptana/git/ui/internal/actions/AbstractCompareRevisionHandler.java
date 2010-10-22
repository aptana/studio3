package com.aptana.git.ui.internal.actions;

import java.util.Collection;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IResource;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;

abstract class AbstractCompareRevisionHandler extends AbstractGitHandler
{

	private boolean enabled;

	@Override
	public boolean isEnabled()
	{
		return this.enabled;
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		this.evalContext = (IEvaluationContext) evaluationContext;
		try
		{
			this.enabled = calculateEnabled();
		}
		finally
		{
			this.evalContext = null;
		}
	}

	private boolean calculateEnabled()
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
