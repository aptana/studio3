package com.aptana.git.ui.internal.actions;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.ui.history.FileRevisionTypedElement;
import org.eclipse.team.ui.synchronize.SaveableCompareEditorInput;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.actions.GitAction;
import com.aptana.git.ui.internal.history.GitCompareFileRevisionEditorInput;

@SuppressWarnings("restriction")
public class CompareWithHEADAction extends GitAction
{

	@Override
	public void run()
	{
		IResource[] resources = getSelectedResources();
		if (resources == null || resources.length != 1)
			return;
		IResource blah = resources[0];
		if (blah.getType() != IResource.FILE)
			return;
		GitRepository repo = getGitRepositoryManager().getAttached(blah.getProject());
		if (repo == null)
			return;
		String name = repo.getChangedFileForResource(blah).getPath();
		IFile file = (IFile) blah;
		ITypedElement base = SaveableCompareEditorInput.createFileElement(file);
		final IFileRevision nextFile = GitPlugin.revisionForCommit(new GitCommit(repo, "HEAD"), name);
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
		IResource blah = resources[0];
		if (blah.getType() != IResource.FILE)
			return false;
		GitRepository repo = getGitRepositoryManager().getAttached(blah.getProject());
		if (repo == null)
			return false;
		ChangedFile file = repo.getChangedFileForResource(blah);
		if (file == null)
			return false;
		return file.hasStagedChanges() || file.hasUnstagedChanges() || file.hasUnmergedChanges();
	}
}
