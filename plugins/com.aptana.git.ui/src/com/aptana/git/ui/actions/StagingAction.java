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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.actions.ChangedFileAction;

abstract class StagingAction extends ChangedFileAction
{

	@Override
	public void run()
	{
		Map<GitRepository, List<ChangedFile>> repoToChangedFiles = new HashMap<GitRepository, List<ChangedFile>>();
		IResource[] resources = getSelectedResources();
		for (IResource resource : resources)
		{
			if (resource instanceof IContainer)
			{
				IContainer container = (IContainer) resource;
				GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
				List<ChangedFile> files = repo.getChangedFilesForContainer(container);
				// FIXME just filter and add them all at the same time!
				for (ChangedFile file : files)
				{
					if (!changedFileIsValid(file))
						continue;

					List<ChangedFile> changedFiles = repoToChangedFiles.get(repo);
					if (changedFiles == null)
					{
						changedFiles = new ArrayList<ChangedFile>();
						repoToChangedFiles.put(repo, changedFiles);
					}
					changedFiles.add(file);
				}
			}
			else
			{
				ChangedFile correspondingChangedFile = getChangedFile(resource);
				if (!changedFileIsValid(correspondingChangedFile))
					continue;
				GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
				List<ChangedFile> changedFiles = repoToChangedFiles.get(repo);
				if (changedFiles == null)
				{
					changedFiles = new ArrayList<ChangedFile>();
					repoToChangedFiles.put(repo, changedFiles);
				}
				changedFiles.add(correspondingChangedFile);
			}
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
		if (resources.length == 0)
		{
			return false;
		}
		for (IResource resource : resources)
		{
			if (!isEnabledForResource(resource))
				return false;
		}
		return true;
	}

	private boolean isEnabledForResource(IResource resource)
	{
		if (resource instanceof IContainer)
		{
			IContainer container = (IContainer) resource;
			GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
			List<ChangedFile> files = repo.getChangedFilesForContainer(container);
			for (ChangedFile file : files)
			{
				if (changedFileIsValid(file))
					return true;
			}
		}
		return changedFileIsValid(getChangedFile(resource));
	}

	protected abstract boolean changedFileIsValid(ChangedFile correspondingChangedFile);
}
