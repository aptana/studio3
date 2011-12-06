/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.util.Collection;
import java.util.List;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.ui.history.FileRevisionTypedElement;
import org.eclipse.team.ui.synchronize.SaveableCompareEditorInput;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevList;
import com.aptana.git.core.model.GitRevSpecifier;
import com.aptana.git.ui.dialogs.CompareWithDialog;
import com.aptana.git.ui.internal.history.GitCompareFileRevisionEditorInput;

@SuppressWarnings("restriction")
public class CompareWithRevisionHandler extends AbstractCompareRevisionHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		Collection<IResource> resources = getSelectedResources();
		if (resources == null || resources.isEmpty())
		{
			return null;
		}
		IResource resource = resources.iterator().next();
		if (resource == null || resource.getType() != IResource.FILE)
		{
			return null;
		}
		GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
		if (repo == null)
		{
			return null;
		}

		// Need the repo relative path
		IPath resourcePath = repo.relativePath(resource);

		// Grab the list of all revisions for this file and show them in a UI list
		GitRevList revList = new GitRevList(repo);
		repo.lazyReload();
		revList.walkRevisionListWithSpecifier(new GitRevSpecifier(resourcePath.toOSString()), new NullProgressMonitor());
		final List<GitCommit> commits = revList.getCommits();

		CompareWithDialog dialog = new CompareWithDialog(getShell(), repo, commits);
		if (dialog.open() == Window.CANCEL)
		{
			return null;
		}
		GitCommit commit = dialog.getRefCommit();

		ITypedElement base = SaveableCompareEditorInput.createFileElement((IFile) resource);
		final IFileRevision nextFile = GitPlugin.revisionForCommit(commit, resourcePath);
		final ITypedElement next = new FileRevisionTypedElement(nextFile);
		final GitCompareFileRevisionEditorInput in = new GitCompareFileRevisionEditorInput(base, next, null);
		CompareUI.openCompareEditor(in);

		return null;
	}

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
			return true;
		}
		return false;
	}

}
