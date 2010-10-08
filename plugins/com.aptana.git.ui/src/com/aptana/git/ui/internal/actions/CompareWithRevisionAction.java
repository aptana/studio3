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
package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.ui.history.FileRevisionTypedElement;
import org.eclipse.team.ui.synchronize.SaveableCompareEditorInput;
import org.eclipse.ui.dialogs.ListDialog;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevList;
import com.aptana.git.core.model.GitRevSpecifier;
import com.aptana.git.ui.actions.GitAction;
import com.aptana.git.ui.internal.history.GitCompareFileRevisionEditorInput;

@SuppressWarnings("restriction")
public class CompareWithRevisionAction extends GitAction
{

	@Override
	public void run()
	{
		IResource[] resources = getSelectedResources();
		if (resources == null || resources.length != 1)
			return;
		IResource resource = resources[0];
		if (resource.getType() != IResource.FILE)
			return;
		GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
		if (repo == null)
			return;

		// Need the repo relative path
		IPath resourcePath = repo.relativePath(resource);

		// Grab the list of all revisions for this file and show them in a UI list
		GitRevList revList = new GitRevList(repo);
		repo.lazyReload();
		revList.walkRevisionListWithSpecifier(new GitRevSpecifier(resourcePath.toOSString()), new NullProgressMonitor());
		final List<GitCommit> commits = revList.getCommits();

		ListDialog dialog = new ListDialog(getShell());
		dialog.setLabelProvider(new LabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				if (element instanceof GitCommit)
				{
					GitCommit commit = (GitCommit) element;
					return MessageFormat.format("{0} {1}", commit.sha(), new Date(commit.getTimestamp())); //$NON-NLS-1$
				}
				return super.getText(element);
			}
		});
		dialog.setContentProvider(ArrayContentProvider.getInstance());
		dialog.setInput(commits.toArray(new GitCommit[0]));

		// InputDialog dialog = new InputDialog(getShell(), "Select Revision", "Enter revision SHA", "", null);
		if (dialog.open() == Window.CANCEL)
		{
			return;
		}
		Object[] selected = dialog.getResult();
		GitCommit commit = (GitCommit) selected[0];

		// String sha = dialog.getValue();
		// GitCommit commit = new GitCommit(repo, sha);

		ITypedElement base = SaveableCompareEditorInput.createFileElement((IFile) resource);
		final IFileRevision nextFile = GitPlugin.revisionForCommit(commit, resourcePath.toPortableString());
		final ITypedElement next = new FileRevisionTypedElement(nextFile);
		final GitCompareFileRevisionEditorInput in = new GitCompareFileRevisionEditorInput(base, next, null);
		CompareUI.openCompareEditor(in);
	}

	@Override
	public boolean isEnabled()
	{
		IResource[] resources = getSelectedResources();
		if (resources == null || resources.length != 1)
			return false;
		IResource resource = resources[0];
		if (resource.getType() != IResource.FILE)
			return false;
		GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
		if (repo == null)
			return false;
		IPath resourcePath = repo.relativePath(resource);
		if (resourcePath == null)
			return false;
		return true;
	}
}
