package com.aptana.git.core;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.team.IMoveDeleteHook;
import org.eclipse.core.resources.team.IResourceTree;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

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

		final GitRepository repo = GitRepository.getAttached(file.getProject());
		if (repo == null)
			return false;

		// Delete the file through the repo
		return repo.deleteFile(getRepoRelativePath(file, repo));
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

		final GitRepository repo = GitRepository.getAttached(folder.getProject());
		if (repo == null)
			return false;

		// Delete the file through the repo
		return repo.deleteFolder(getRepoRelativePath(folder, repo));
	}

	public boolean deleteProject(final IResourceTree tree, final IProject project, final int updateFlags,
			final IProgressMonitor monitor)
	{
		// TODO: Note that eclipse thinks folders are real, while
		// Git does not care.
		return FINISH_FOR_ME;
	}

	public boolean moveFile(final IResourceTree tree, final IFile srcf, final IFile dstf, final int updateFlags,
			final IProgressMonitor monitor)
	{
		final boolean force = (updateFlags & IResource.FORCE) == IResource.FORCE;
		if (!force && !tree.isSynchronized(srcf, IResource.DEPTH_ZERO))
			return false;

		final GitRepository repo = GitRepository.getAttached(srcf.getProject());
		if (repo == null)
			return false;
		final GitRepository dstm = GitRepository.getAttached(dstf.getProject());
		if (dstm == null || !dstm.equals(repo))
			return false;
		// TODO If they're in separate repos, we need to delete and add!

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
		final GitRepository repo = GitRepository.getAttached(srcf.getProject());
		if (repo == null)
			return false;
		final GitRepository dstm = GitRepository.getAttached(dstf.getProject());
		if (dstm == null || !dstm.equals(repo))
			return false;
		// TODO If they're in separate repos, we need to delete and add!

		// Honor the KEEP LOCAL HISTORY update flag!
		if ((updateFlags & IResource.KEEP_HISTORY) == IResource.KEEP_HISTORY)
		{
			// TODO Add all files that will be affected to local history!
//			tree.addToLocalHistory(srcf);
		}
		
		String source = getRepoRelativePath(srcf, repo);
		String dest = getRepoRelativePath(dstf, repo);
		IStatus status = repo.moveFile(source, dest);
		// Call tree.failed if failed, call tree.movedFolder if success
		if (status.isOK())
			tree.movedFolderSubtree(srcf, dstf);
		else
			tree.failed(status);
		return true;
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

	private String getRepoRelativePath(final IResource file, GitRepository repo)
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

}
