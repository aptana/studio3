package com.aptana.git.core;

import java.io.File;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.team.IMoveDeleteHook;
import org.eclipse.core.resources.team.IResourceTree;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRepository;

class GitMoveDeleteHook implements IMoveDeleteHook
{

	private static final boolean I_AM_DONE = true;
	private static final boolean FINISH_FOR_ME = false;

	public boolean deleteFile(final IResourceTree tree, final IFile file, final int updateFlags,
			final IProgressMonitor monitor)
	{
		final boolean force = (updateFlags & IResource.FORCE) == IResource.FORCE;
		if (!force && !tree.isSynchronized(file, IResource.DEPTH_ZERO))
			return false;

		final GitRepository repo = getAttachedGitRepository(file.getProject());
		if (repo == null)
			return false;

		// If this file is new and unstaged, we don't need to handle it!
		ChangedFile changed = repo.getChangedFileForResource(file);
		if (changed == null || changed.getStatus() == ChangedFile.Status.NEW)
			return false;

		if ((updateFlags & IResource.KEEP_HISTORY) == IResource.KEEP_HISTORY)
			tree.addToLocalHistory(file);

		// Delete the file through the repo
		IStatus status = repo.deleteFile(changed.getPath());
		if (status.isOK())
		{
			tree.deletedFile(file);
		}
		else
		{
			tree.failed(status);
		}
		return true;
	}

	public boolean deleteFolder(final IResourceTree tree, final IFolder folder, final int updateFlags,
			final IProgressMonitor monitor)
	{
		// Deleting a GIT repository which is in use is a pretty bad idea. To
		// delete disconnect the team provider first.
		if (folder.getName().equals(GitRepository.GIT_DIR))
		{
			return cannotModifyRepository(tree);
		}

		final boolean force = (updateFlags & IResource.FORCE) == IResource.FORCE;
		if (!force && !tree.isSynchronized(folder, IResource.DEPTH_INFINITE))
			return false;

		final GitRepository repo = getAttachedGitRepository(folder.getProject());
		if (repo == null)
			return false;

		// Honor the KEEP LOCAL HISTORY update flag!
		if ((updateFlags & IResource.KEEP_HISTORY) == IResource.KEEP_HISTORY)
		{
			addFilesToLocalHistoryRecursively(tree, folder);
		}

		// Delete the folder through the repo
		IStatus status = repo.deleteFolder(getRepoRelativePath(folder, repo));
		if (status.isOK())
		{
			tree.deletedFolder(folder);
		}
		else
		{
			tree.failed(status);
		}
		return true;
	}

	public boolean deleteProject(final IResourceTree tree, final IProject project, final int updateFlags,
			final IProgressMonitor monitor)
	{
		// TODO Delete the project through git if it is a subfolder of a git repo (project root isn't root of repo)
		return FINISH_FOR_ME;
	}

	public boolean moveFile(final IResourceTree tree, final IFile srcf, final IFile dstf, final int updateFlags,
			final IProgressMonitor monitor)
	{
		final boolean force = (updateFlags & IResource.FORCE) == IResource.FORCE;
		if (!force && !tree.isSynchronized(srcf, IResource.DEPTH_ZERO))
			return false;

		final GitRepository repo = getAttachedGitRepository(srcf.getProject());
		if (repo == null)
			return false;
		final GitRepository dstm = getAttachedGitRepository(dstf.getProject());
		if (dstm == null || !dstm.equals(repo))
			return false;
		// TODO If they're in separate repos, we need to delete and add!

		// If this file is new and unstaged, we don't need to handle it!
		ChangedFile changed = repo.getChangedFileForResource(srcf);
		if (changed == null || changed.getStatus() == ChangedFile.Status.NEW)
			return false;

		// Honor the KEEP LOCAL HISTORY update flag!
		if ((updateFlags & IResource.KEEP_HISTORY) == IResource.KEEP_HISTORY)
			tree.addToLocalHistory(srcf);

		String source = getRepoRelativePath(srcf, repo);
		String dest = getRepoRelativePath(dstf, repo);
		IStatus status = repo.moveFile(source, dest);
		// Call tree.failed if failed, call tree.movedFile if success
		if (status.isOK())
			tree.movedFile(srcf, dstf);
		else
			tree.failed(status);
		return true;
	}

	public boolean moveFolder(final IResourceTree tree, final IFolder srcf, final IFolder dstf, final int updateFlags,
			final IProgressMonitor monitor)
	{
		final GitRepository repo = getAttachedGitRepository(srcf.getProject());
		if (repo == null)
			return false;
		final GitRepository dstm = getAttachedGitRepository(dstf.getProject());
		if (dstm == null || !dstm.equals(repo))
			return false;
		// TODO If they're in separate repos, we need to delete and add!

		String source = getRepoRelativePath(srcf, repo);
		// If source folder contains no already committed files, we need to punt!
		if (hasNoCommittedFiles(source, repo))
			return false;

		// Honor the KEEP LOCAL HISTORY update flag!
		if ((updateFlags & IResource.KEEP_HISTORY) == IResource.KEEP_HISTORY)
		{
			addFilesToLocalHistoryRecursively(tree, srcf);
		}

		String dest = getRepoRelativePath(dstf, repo);
		IStatus status = repo.moveFile(source, dest);
		// Call tree.failed if failed, call tree.movedFolder if success
		if (status.isOK())
			tree.movedFolderSubtree(srcf, dstf);
		else
			tree.failed(status);
		return true;
	}

	private boolean hasNoCommittedFiles(String source, GitRepository repo)
	{
		int exitCode = 1;
		Map<Integer, String> result = GitExecutable.instance().runInBackground(repo.workingDirectory(), "ls-tree", //$NON-NLS-1$
				"-r", "HEAD:" + source); //$NON-NLS-1$ //$NON-NLS-2$
		if (result != null && !result.isEmpty())
			exitCode = result.keySet().iterator().next();

		return exitCode != 0;
	}

	public boolean moveProject(final IResourceTree tree, final IProject source, final IProjectDescription description,
			final int updateFlags, final IProgressMonitor monitor)
	{
		// TODO: We should be able to do this without too much effort when the
		// projects belong to the same Git repository.
		return FINISH_FOR_ME;
	}

	private boolean cannotModifyRepository(final IResourceTree tree)
	{
		tree.failed(new Status(IStatus.ERROR, GitPlugin.getPluginId(), 0,
				Messages.GitMoveDeleteHook_CannotModifyRepository_ErrorMessage, null));
		return I_AM_DONE;
	}

	protected String getRepoRelativePath(final IResource file, GitRepository repo)
	{
		String workingDir = repo.workingDirectory();
		String filePath = file.getLocationURI().getPath();
		if (filePath.startsWith(workingDir))
		{
			filePath = filePath.substring(workingDir.length());
			if (filePath.startsWith(File.separator))
				filePath = filePath.substring(1);
		}
		return filePath;
	}

	protected GitRepository getAttachedGitRepository(IProject project)
	{
		return GitRepository.getAttached(project);
	}

	/**
	 * Traverses a folder to infinite depth, adding each file visited to the IResourceTree's local file history.
	 * 
	 * @param tree
	 * @param folder
	 */
	private void addFilesToLocalHistoryRecursively(final IResourceTree tree, IFolder folder)
	{
		try
		{
			folder.accept(new IResourceVisitor()
			{

				@Override
				public boolean visit(IResource resource) throws CoreException
				{
					if (resource instanceof IFile)
					{
						tree.addToLocalHistory((IFile) resource);
					}
					return true;
				}
			});
		}
		catch (CoreException e)
		{
			GitPlugin.logError(e);
		}
	}

}
