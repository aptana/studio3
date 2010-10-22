package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevList;
import com.aptana.git.core.model.GitRevSpecifier;
import com.aptana.git.ui.internal.history.GitCompareFileRevisionEditorInput;

@SuppressWarnings("restriction")
public class CompareWithRevisionHandler extends AbstractCompareRevisionHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		Collection<IResource> resources = getSelectedResources();
		if (resources == null || resources.size() != 1)
		{
			return null;
		}
		IResource resource = resources.iterator().next();
		if (resource.getType() != IResource.FILE)
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

		ListDialog dialog = new ListDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
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
			return null;
		}
		Object[] selected = dialog.getResult();
		GitCommit commit = (GitCommit) selected[0];

		// String sha = dialog.getValue();
		// GitCommit commit = new GitCommit(repo, sha);

		ITypedElement base = SaveableCompareEditorInput.createFileElement((IFile) resource);
		final IFileRevision nextFile = GitPlugin.revisionForCommit(commit, resourcePath);
		final ITypedElement next = new FileRevisionTypedElement(nextFile);
		final GitCompareFileRevisionEditorInput in = new GitCompareFileRevisionEditorInput(base, next, null);
		CompareUI.openCompareEditor(in);

		return null;
	}

}
