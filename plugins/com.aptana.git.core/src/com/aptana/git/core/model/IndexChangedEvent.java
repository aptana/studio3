package com.aptana.git.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

public class IndexChangedEvent extends RepositoryEvent
{

	private Collection<ChangedFile> postChangeFiles;
	private Collection<ChangedFile> preChangeFiles;
	private ArrayList<ChangedFile> diff;

	IndexChangedEvent(GitRepository repository, Collection<ChangedFile> preChangeFiles,
			Collection<ChangedFile> postChangeFiles)
	{
		super(repository);
		this.preChangeFiles = preChangeFiles;
		this.postChangeFiles = postChangeFiles;
	}
	
	public boolean hasDiff()
	{
		return !getDiff().isEmpty();
	}

	public Set<IResource> getFilesWithChanges()
	{
		Collection<ChangedFile> changedFiles = getDiff();

		GitRepository repo = getRepository();
		IPath workingDirectory = repo.workingDirectory();
		Set<IResource> files = new HashSet<IResource>();
		for (ChangedFile changedFile : changedFiles)
		{
			IPath path = workingDirectory.append(changedFile.getPath()).makeAbsolute();
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
			if (file == null)
				continue;
			files.add(file);
		}
		return files;
	}

	private synchronized Collection<ChangedFile> getDiff()
	{
		if (diff == null)
		{
			// Go through each of the lists and collect any that are in one but not the other, and any that are in both
			// but have different statuses
			diff = new ArrayList<ChangedFile>();
			for (ChangedFile file : preChangeFiles)
			{
				if (!contains(postChangeFiles, file))
					diff.add(file);
			}
			for (ChangedFile file : postChangeFiles)
			{
				if (!contains(preChangeFiles, file))
					diff.add(file);
			}
		}
		return diff;
	}

	private boolean contains(Collection<ChangedFile> files, ChangedFile file)
	{
		for (ChangedFile aFile : files)
		{
			if ((file.hasStagedChanges == aFile.hasStagedChanges)
					&& (file.hasUnstagedChanges == aFile.hasUnstagedChanges)
					&& (aFile.toString().equals(file.toString())))
			{
				return true;
			}
		}
		return false;
	}

}
