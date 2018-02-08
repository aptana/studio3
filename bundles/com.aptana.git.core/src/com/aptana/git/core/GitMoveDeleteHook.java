/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core;

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.team.IMoveDeleteHook;
import org.eclipse.core.resources.team.IResourceTree;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;

import com.aptana.core.logging.IdeLog;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;

class GitMoveDeleteHook implements IMoveDeleteHook
{

	static final Status CANNOT_MODIFY_REPO = new Status(IStatus.ERROR, GitPlugin.getPluginId(), 0,
			Messages.GitMoveDeleteHook_CannotModifyRepository_ErrorMessage, null);
	private static final boolean I_AM_DONE = true;

	public boolean deleteFile(final IResourceTree tree, final IFile file, final int updateFlags, // NO_UCD
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
		IStatus status = repo.deleteFile(changed.getRelativePath());
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

	public boolean deleteFolder(final IResourceTree tree, final IFolder folder, final int updateFlags, // NO_UCD
			final IProgressMonitor monitor)
	{
		final boolean force = (updateFlags & IResource.FORCE) == IResource.FORCE;
		if (!force && !tree.isSynchronized(folder, IResource.DEPTH_INFINITE))
			return false;

		final GitRepository repo = getAttachedGitRepository(folder.getProject());
		if (repo == null)
			return false;

		// Deleting a GIT repository which is in use is a pretty bad idea. To
		// delete disconnect the team provider first.
		if (folder.getName().equals(GitRepository.GIT_DIR))
		{
			return cannotModifyRepository(tree);
		}

		IPath source = getRepoRelativePath(folder, repo);
		// If project contains no already committed files, we need to punt!
		if (hasNoCommittedFiles(source, repo))
			return false;

		// Honor the KEEP LOCAL HISTORY update flag!
		if ((updateFlags & IResource.KEEP_HISTORY) == IResource.KEEP_HISTORY)
		{
			addFilesToLocalHistoryRecursively(tree, folder);
		}

		// Delete the folder through the repo
		IStatus status = repo.deleteFolder(source);
		if (status.isOK())
		{
			tree.deletedFolder(folder);
		}
		// Git reports that no files match the path specified. Let's punt and let the normal resource deletion happen...
		else if (noMatchingFiles(status))
		{
			tree.standardDeleteFolder(folder, updateFlags, monitor);
		}
		else
		{
			tree.failed(status);
		}
		return true;
	}

	/**
	 * Did git report that no files match?
	 * 
	 * @param status
	 * @return
	 */
	protected boolean noMatchingFiles(IStatus status)
	{
		return status.getCode() == 128 && status.getMessage().endsWith("did not match any files"); //$NON-NLS-1$
	}

	public boolean deleteProject(final IResourceTree tree, final IProject project, int updateFlags, // NO_UCD
			final IProgressMonitor monitor)
	{
		final GitRepository repo = getAttachedGitRepository(project);
		if (repo == null)
			return false;

		// force is implied by always delete...
		boolean alwaysDeleteContent = (updateFlags & IResource.ALWAYS_DELETE_PROJECT_CONTENT) != 0;
		// If repo root is same as project root, we need to just punt and return false
		// so filesystem takes care of it
		try
		{
			if (repo.workingDirectory().toFile().getCanonicalPath()
					.equals(project.getLocation().toFile().getCanonicalPath()))
			{
				getGitRepositoryManager().removeRepository(project);
				if (alwaysDeleteContent)
				{
					// Force delete the .git dir, since it's probably out of sync and not forcing could cause project
					// delete to fail!
					IFolder gitDir = project.getFolder(GitRepository.GIT_DIR);
					if (gitDir.exists())
					{
						tree.standardDeleteFolder(gitDir, updateFlags | IResource.FORCE, new NullProgressMonitor()); // TODO
																														// Use
																														// a
																														// submonitor
																														// here?
						tree.deletedFolder(gitDir);
					}
				}
				return false;
			}
		}
		catch (IOException e)
		{
			IdeLog.logError(GitPlugin.getDefault(), "File.getCanonicalPath failed.", e, IDebugScopes.DEBUG); //$NON-NLS-1$
		}

		IPath source = getRepoRelativePath(project, repo);
		// If project contains no already committed files, we need to punt!
		if (hasNoCommittedFiles(source, repo))
			return false;

		boolean force = alwaysDeleteContent || (updateFlags & IResource.FORCE) == IResource.FORCE;
		if (force)
		{
			updateFlags |= IResource.FORCE;
		}
		if (!force && !tree.isSynchronized(project, IResource.DEPTH_INFINITE))
			return false;
		// FIXME Should we return true, but call tree.failed if unsynched?

		// We may not actually need to delete the contents....
		boolean neverDeleteContent = (updateFlags & IResource.NEVER_DELETE_PROJECT_CONTENT) != 0;
		boolean deleteContents = alwaysDeleteContent || (project.isOpen() && !neverDeleteContent);

		IStatus status = Status.OK_STATUS;
		if (deleteContents)
		{
			// Delete the project through the repo
			status = repo.deleteFolder(source);
		}

		if (status.isOK())
		{
			tree.deletedProject(project);
		}
		// Git reports that no files match the path specified. Let's punt and let the normal resource deletion happen...
		else if (noMatchingFiles(status))
		{
			tree.standardDeleteProject(project, updateFlags, monitor);
		}
		else
		{
			tree.failed(status);
		}
		return true;
	}

	public boolean moveFile(final IResourceTree tree, final IFile srcf, final IFile dstf, final int updateFlags,
			final IProgressMonitor monitor)
	{
		final boolean force = (updateFlags & IResource.FORCE) == IResource.FORCE;
		if (!force && !tree.isSynchronized(srcf, IResource.DEPTH_ZERO))
		{
			return false;
		}

		final GitRepository repo = getAttachedGitRepository(srcf.getProject());
		if (repo == null)
		{
			return false;
		}
		final GitRepository dstm = getAttachedGitRepository(dstf.getProject());
		if (dstm == null || !dstm.equals(repo))
		{
			return false;
		}
		// TODO If they're in separate repos, we need to delete and add!

		// If this file is new and unstaged, we don't need to handle it!
		ChangedFile changed = repo.getChangedFileForResource(srcf);
		if (changed == null || changed.getStatus() == ChangedFile.Status.NEW)
			return false;

		// Honor the KEEP LOCAL HISTORY update flag!
		if ((updateFlags & IResource.KEEP_HISTORY) == IResource.KEEP_HISTORY)
			tree.addToLocalHistory(srcf);

		IPath source = getRepoRelativePath(srcf, repo);
		IPath dest = getRepoRelativePath(dstf, repo);
		IStatus status = repo.moveFile(source, dest);
		// Call tree.failed if failed, call tree.movedFile if success
		if (status.isOK())
		{
			tree.movedFile(srcf, dstf);
		}
		else
		{
			tree.failed(status);
		}
		return true;
	}

	public boolean moveFolder(final IResourceTree tree, final IFolder srcf, final IFolder dstf, final int updateFlags,
			final IProgressMonitor monitor)
	{
		final GitRepository repo = getAttachedGitRepository(srcf.getProject());
		if (repo == null)
		{
			return false;
		}
		final GitRepository dstm = getAttachedGitRepository(dstf.getProject());
		if (dstm == null || !dstm.equals(repo))
		{
			return false;
		}
		// TODO If they're in separate repos, we need to delete and add!

		IPath source = getRepoRelativePath(srcf, repo);
		// If source folder contains no already committed files, we need to punt!
		if (hasNoCommittedFiles(source, repo))
		{
			return false;
		}

		// Honor the KEEP LOCAL HISTORY update flag!
		if ((updateFlags & IResource.KEEP_HISTORY) == IResource.KEEP_HISTORY)
		{
			addFilesToLocalHistoryRecursively(tree, srcf);
		}

		IPath dest = getRepoRelativePath(dstf, repo);
		IStatus status = repo.moveFile(source, dest);
		// Call tree.failed if failed, call tree.movedFolder if success
		if (status.isOK())
		{
			tree.movedFolderSubtree(srcf, dstf);
		}
		else
		{
			tree.failed(status);
		}
		return true;
	}

	/**
	 * This checks to see if any files are in the index. But they may have been deleted on disk already, which results
	 * in us getting a 128 error code running rm -rf later.
	 * 
	 * @param source
	 * @param repo
	 * @return
	 */
	protected boolean hasNoCommittedFiles(IPath source, GitRepository repo)
	{
		IStatus result = repo.execute(GitRepository.ReadWrite.READ, "ls-tree", "-r", "HEAD:" + source.toOSString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return result == null || !result.isOK();
	}

	public boolean moveProject(final IResourceTree tree, final IProject source, final IProjectDescription description,
			final int updateFlags, final IProgressMonitor monitor)
	{
		GitRepository repo = getAttachedGitRepository(source);
		if (repo == null)
		{
			// no git support attached, just let eclipse handle moving this project.
			return false;
		}

		IPath workingDirectory = repo.workingDirectory();

		// Project root = git root, so unattach, move, re-attach
		if (workingDirectory.equals(source.getLocation()))
		{
			// Unattach our git support and stop all actions on this repo!
			getGitRepositoryManager().removeRepository(source);

			// Move the project in a standard way
			tree.standardMoveProject(source, description, updateFlags, monitor);

			// Now re-attach our git support to the new location?
			try
			{
				getGitRepositoryManager().attachExisting(source, monitor);
			}
			catch (CoreException e)
			{
				// ignore
			}

			return true;
		}

		IPath destPath = description.getLocation();
		// location may be null for default location!
		if (destPath == null)
		{
			destPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(description.getName());
		}

		// What if the destination is not under the repo? Unattach our support, move it and return
		if (!workingDirectory.isPrefixOf(destPath))
		{
			// Unattach our git support and stop all actions on this repo!
			getGitRepositoryManager().removeRepository(source);

			// Move the project in a standard way
			tree.standardMoveProject(source, description, updateFlags, monitor);
			return true;
		}
		destPath = destPath.makeRelativeTo(workingDirectory);

		// Moving project underneath repo root to another location under repo, handle like moving a folder.
		IPath sourcePath = getRepoRelativePath(source, repo);
		// If source folder contains no already committed files, we need to punt!
		if (hasNoCommittedFiles(sourcePath, repo))
		{
			return false;
		}

		// Honor the KEEP LOCAL HISTORY update flag!
		if ((updateFlags & IResource.KEEP_HISTORY) == IResource.KEEP_HISTORY)
		{
			addFilesToLocalHistoryRecursively(tree, source);
		}

		IStatus status = repo.moveFile(sourcePath, destPath);
		// Call tree.failed if failed, call tree.movedProjectSubtree if success
		if (status.isOK())
		{
			tree.movedProjectSubtree(source, description);
		}
		else
		{
			tree.failed(status);
		}
		return true;
	}

	private boolean cannotModifyRepository(final IResourceTree tree)
	{
		tree.failed(CANNOT_MODIFY_REPO);
		return I_AM_DONE;
	}

	private IPath getRepoRelativePath(final IResource file, GitRepository repo)
	{
		return repo.relativePath(file);
	}

	protected GitRepository getAttachedGitRepository(IProject project)
	{
		return getGitRepositoryManager().getAttached(project);
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	/**
	 * Traverses a folder to infinite depth, adding each file visited to the IResourceTree's local file history.
	 * 
	 * @param tree
	 * @param folder
	 */
	protected void addFilesToLocalHistoryRecursively(final IResourceTree tree, IContainer folder)
	{
		try
		{
			folder.accept(new IResourceVisitor()
			{

				public boolean visit(IResource resource)
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
			IdeLog.logError(GitPlugin.getDefault(), e, IDebugScopes.DEBUG);
		}
	}

}
