package com.aptana.explorer.internal.ui;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;

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
		GitRepository repo = GitRepository.getAttached(resource.getProject());
		if (repo == null)
			return false;

		List<ChangedFile> changedFiles = repo.index().changedFiles();
		if (changedFiles == null || changedFiles.isEmpty())
			return false;

		String workingDirectory = repo.workingDirectory();
		if (!workingDirectory.endsWith("/")) //$NON-NLS-1$
		{
			workingDirectory += "/"; //$NON-NLS-1$
		}

		for (ChangedFile changedFile : changedFiles)
		{
			String fullPath = workingDirectory + changedFile.getPath();
			String resourcePath = resource.getLocationURI().getPath();
			if (fullPath.startsWith(resourcePath))
				return true;

		}
		return false;
	}

}
