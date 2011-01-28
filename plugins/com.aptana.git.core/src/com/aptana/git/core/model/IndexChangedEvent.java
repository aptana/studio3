/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import com.aptana.core.util.CollectionsUtil;

public class IndexChangedEvent extends RepositoryEvent
{

	private Collection<ChangedFile> postChangeFiles;
	private Collection<ChangedFile> preChangeFiles;
	private Collection<ChangedFile> diff;

	IndexChangedEvent(GitRepository repository, Collection<ChangedFile> preChangeFiles,
			Collection<ChangedFile> postChangeFiles)
	{
		super(repository);
		this.preChangeFiles = preChangeFiles;
		this.postChangeFiles = postChangeFiles;
	}

	protected boolean hasDiff()
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
			diff = CollectionsUtil.getNonOverlapping(preChangeFiles, postChangeFiles);
		}
		return diff;
	}

}
