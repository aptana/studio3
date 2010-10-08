/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.actions;

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
import com.aptana.git.ui.internal.actions.Messages;

public class RevertAction extends StagingAction
{
	
	@Override
	public String getText()
	{
		return Messages.RevertAction_Label;
	}
	
	@Override
	protected void doOperation(GitRepository repo, final List<ChangedFile> changedFiles)
	{
		final Set<IResource> changedResources = new HashSet<IResource>();
		for (IResource resource : getSelectedResources())
		{
			for (ChangedFile changedFile : changedFiles)
			{
				if (new File(resource.getLocationURI()).getAbsolutePath().endsWith(changedFile.getPath()))
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
