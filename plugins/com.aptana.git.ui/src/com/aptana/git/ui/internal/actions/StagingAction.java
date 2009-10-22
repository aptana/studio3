package com.aptana.git.ui.internal.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;

abstract class StagingAction extends ChangedFileAction
{

	@Override
	protected void execute(IAction action) throws InvocationTargetException, InterruptedException
	{
		Map<GitRepository, List<ChangedFile>> repoToChangedFiles = new HashMap<GitRepository, List<ChangedFile>>();
		IResource[] resources = getSelectedResources();
		for (IResource resource : resources)
		{
			ChangedFile correspondingChangedFile = getChangedFile(resource);
			if (!changedFileIsValid(correspondingChangedFile))
				continue;
			GitRepository repo = GitRepository.instance(resource.getProject());
			List<ChangedFile> changedFiles = repoToChangedFiles.get(repo);
			if (changedFiles == null)
			{
				changedFiles = new ArrayList<ChangedFile>();
				repoToChangedFiles.put(repo, changedFiles);
			}
			changedFiles.add(correspondingChangedFile);
		}

		for (Map.Entry<GitRepository, List<ChangedFile>> entry : repoToChangedFiles.entrySet())
		{
			doOperation(entry.getKey(), entry.getValue());
		}
	}

	protected abstract void doOperation(GitRepository repo, List<ChangedFile> changedFiles);

	@Override
	public boolean isEnabled()
	{
		IResource[] resources = getSelectedResources();
		for (IResource resource : resources)
		{
			if (!isEnabledForResource(resource))
				return false;
		}
		return true;
	}

	private boolean isEnabledForResource(IResource resource)
	{
		return changedFileIsValid(getChangedFile(resource));
	}

	protected abstract boolean changedFileIsValid(ChangedFile correspondingChangedFile);
}
