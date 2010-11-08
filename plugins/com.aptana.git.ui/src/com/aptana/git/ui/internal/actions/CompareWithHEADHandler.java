package com.aptana.git.ui.internal.actions;

import java.util.Collection;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
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
		if (resources == null || resources.size() != 1)
		{
			return null;
		}
		IResource blah = resources.iterator().next();
		if (blah.getType() != IResource.FILE)
		{
			return null;
		}
		GitRepository repo = getGitRepositoryManager().getAttached(blah.getProject());
		if (repo == null)
		{
			return null;
		}
		String name = repo.getChangedFileForResource(blah).getPath();
		IFile file = (IFile) blah;
		ITypedElement base = SaveableCompareEditorInput.createFileElement(file);
		final IFileRevision nextFile = GitPlugin
				.revisionForCommit(new GitCommit(repo, "HEAD"), Path.fromOSString(name)); //$NON-NLS-1$
		final ITypedElement next = new FileRevisionTypedElement(nextFile);
		final GitCompareFileRevisionEditorInput in = new GitCompareFileRevisionEditorInput(base, next, null);
		CompareUI.openCompareEditor(in);
		return null;
	}
}
