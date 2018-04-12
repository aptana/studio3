/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.util.Collection;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.ui.history.FileRevisionTypedElement;
import org.eclipse.team.ui.synchronize.SaveableCompareEditorInput;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.history.GitCompareFileRevisionEditorInput;

@SuppressWarnings("restriction")
public class CompareWithHEADHandler extends AbstractCompareRevisionHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		// TODO Allow params so user can set the SHA/branch to compare with!
		Collection<IResource> resources = getSelectedResources();
		if (resources == null || resources.isEmpty())
		{
			return null;
		}
		for (IResource blah : resources)
		{
			if (blah.getType() != IResource.FILE)
			{
				continue;
			}
			GitRepository repo = getGitRepositoryManager().getAttached(blah.getProject());
			if (repo == null)
			{
				continue;
			}
			IPath name = repo.getChangedFileForResource(blah).getRelativePath();
			IFile file = (IFile) blah;
			ITypedElement base = SaveableCompareEditorInput.createFileElement(file);
			final IFileRevision nextFile = GitPlugin.revisionForCommit(new GitCommit(repo, "HEAD"), name); //$NON-NLS-1$
			final ITypedElement next = new FileRevisionTypedElement(nextFile);
			final GitCompareFileRevisionEditorInput in = new GitCompareFileRevisionEditorInput(base, next, null);
			CompareUI.openCompareEditor(in);
		}
		return null;
	}
}
