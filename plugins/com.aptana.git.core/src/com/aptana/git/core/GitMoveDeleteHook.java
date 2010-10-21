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
package com.aptana.git.core;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.team.IMoveDeleteHook;
import org.eclipse.core.resources.team.IResourceTree;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;

class GitMoveDeleteHook implements IMoveDeleteHook
{

	static final Status CANNOT_MODIFY_REPO = new Status(IStatus.ERROR, GitPlugin.getPluginId(), 0,
			Messages.GitMoveDeleteHook_CannotModifyRepository_ErrorMessage, null);
	private static final boolean I_AM_DONE = true;
	private static final boolean FINISH_FOR_ME = false;

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
		else
		{
			tree.failed(status);
		}
		return true;
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
		try {
			if (repo.workingDirectory().toFile().getCanonicalPath()
					.equals(project.getLocation().toFile().getCanonicalPath()))
			{
				getGitRepositoryManager().removeRepository(project);
				if (alwaysDeleteContent)
				{
					// Force delete the .git dir, since it's probably out of sync and not forcing could cause project delete to fail!
					IFolder gitDir = project.getFolder(GitRepository.GIT_DIR);
					if (gitDir.exists())
					{
						tree.standardDeleteFolder(gitDir, updateFlags | IResource.FORCE,
							new NullProgressMonitor()); // TODO Use a submonitor here?
						tree.deletedFolder(gitDir);
					}
				}
				return false;
			}
		} catch (IOException e) {
			GitPlugin.logError("File.getCanonicalPath failed.", e); //$NON-NLS-1$
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

		IStatus status;
		if (deleteContents)
		{
			// Delete the project through the repo
			status = repo.deleteFolder(source);
		}
		else
		{
			status = Status.OK_STATUS;
		}

		if (status.isOK())
		{
			tree.deletedProject(project);
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

		IPath source = getRepoRelativePath(srcf, repo);
		IPath dest = getRepoRelativePath(dstf, repo);
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

		IPath source = getRepoRelativePath(srcf, repo);
		// If source folder contains no already committed files, we need to punt!
		if (hasNoCommittedFiles(source, repo))
			return false;

		// Honor the KEEP LOCAL HISTORY update flag!
		if ((updateFlags & IResource.KEEP_HISTORY) == IResource.KEEP_HISTORY)
		{
			addFilesToLocalHistoryRecursively(tree, srcf);
		}

		IPath dest = getRepoRelativePath(dstf, repo);
		IStatus status = repo.moveFile(source, dest);
		// Call tree.failed if failed, call tree.movedFolder if success
		if (status.isOK())
			tree.movedFolderSubtree(srcf, dstf);
		else
			tree.failed(status);
		return true;
	}

	protected boolean hasNoCommittedFiles(IPath source, GitRepository repo)
	{
		int exitCode = 1;
		Map<Integer, String> result = GitExecutable.instance().runInBackground(repo.workingDirectory(), "ls-tree", //$NON-NLS-1$
				"-r", "HEAD:" + source.toOSString()); //$NON-NLS-1$ //$NON-NLS-2$
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
		tree.failed(CANNOT_MODIFY_REPO);
		return I_AM_DONE;
	}

	private IPath getRepoRelativePath(final IResource file, GitRepository repo)
	{
		IPath workingDir = repo.workingDirectory();
		IPath filePath = file.getLocation();
		if (workingDir.isPrefixOf(filePath))
		{
			return filePath.makeRelativeTo(workingDir);
		}
		return filePath;
	}

	protected GitRepository getAttachedGitRepository(IProject project)
	{
		return getGitRepositoryManager().getAttached(project);
	}

	private IGitRepositoryManager getGitRepositoryManager()
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
