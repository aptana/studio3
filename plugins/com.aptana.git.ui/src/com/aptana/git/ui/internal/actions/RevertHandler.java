/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;

public class RevertHandler extends AbstractStagingHandler
{

	@Override
	protected void doOperation(GitRepository repo, List<ChangedFile> changedFiles)
	{
		final Set<IResource> changedResources = new HashSet<IResource>();
		for (IResource resource : getSelectedResources())
		{
			File file = new File(resource.getLocationURI());
			for (ChangedFile changedFile : changedFiles)
			{
				// FIXME We should be able to ask for the full path for the changed file by appending to the git repo's working directory!
				if (file.getAbsolutePath().endsWith(changedFile.getRelativePath().toOSString()))
				{
					changedResources.add(resource);
					break;
				}
			}
		}
		repo.index().discardChangesForFiles(changedFiles);
		WorkspaceJob job = new WorkspaceJob(Messages.RevertAction_RefreshJob_Title)
		{

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
			{
				int work = 100 * changedResources.size();
				SubMonitor sub = SubMonitor.convert(monitor, work);
				for (IResource resource : changedResources)
				{
					if (sub.isCanceled())
						return Status.CANCEL_STATUS;
					resource.refreshLocal(IResource.DEPTH_INFINITE, sub.newChild(100));
				}
				sub.done();
				return Status.OK_STATUS;
			}
		};
		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		job.setUser(true);
		job.schedule();
	}

	@Override
	protected boolean changedFileIsValid(ChangedFile correspondingChangedFile)
	{
		return correspondingChangedFile != null && correspondingChangedFile.hasUnstagedChanges()
				&& correspondingChangedFile.getStatus() == ChangedFile.Status.MODIFIED;
	}

}
