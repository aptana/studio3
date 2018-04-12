/**
 * Aptana Studio
 * Copyright (c) 2005-2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import java.text.MessageFormat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;

import com.aptana.core.logging.IdeLog;
import com.aptana.git.core.GitPlugin;

/**
 * A value object representing a changed file in a git repo. There can be a large number of these and they can be
 * constantly sorted/hashed/compared. Ideally we don't want to modify the state of a ChangedFile, but to generate a new
 * instance with the changes we want. However, we still do cheat in a handful of cases where we mark the staged/unstaged
 * booleans.
 * 
 * @author cwilliams
 */
public class ChangedFile implements Comparable<ChangedFile>
{

	public enum Status
	{
		NEW, DELETED, MODIFIED, UNMERGED
	}

	/**
	 * Used to make copies of a changed file, particularly when existing changed files are going to get modified by an
	 * operation and we need to refer to their original state.
	 * 
	 * @param other
	 */
	private ChangedFile(ChangedFile other)
	{
		this(other.repo, other.path, other.status, other.commitBlobMode, other.commitBlobSHA, other.hasStagedChanges,
				other.hasUnstagedChanges);
	}

	// FIXME can we enforce that a ChangedFile cannot live outside it's repo? What sort of sanity checking can we do
	// against the repo/location?
	public ChangedFile(GitRepository repository, IPath path, Status status, String mode, String sha, boolean staged,
			boolean unstaged)
	{
		this.repo = repository;
		this.path = path;
		this.status = status;
		this.commitBlobMode = mode;
		this.commitBlobSHA = sha;
		this.hasStagedChanges = staged;
		this.hasUnstagedChanges = unstaged;
	}

	private final GitRepository repo;
	private final IPath path;
	final Status status;
	private boolean hasStagedChanges;
	private boolean hasUnstagedChanges;
	private final String commitBlobSHA;
	private final String commitBlobMode;

	/**
	 * Returns the path relative to the repo root.
	 * 
	 * @return
	 */
	public IPath getRelativePath()
	{
		return path;
	}

	public Status getStatus()
	{
		return status;
	}

	/**
	 * Forces this to be marked as having staged changes, no unstaged changes.
	 */
	public void makeStaged()
	{
		hasUnstagedChanges = false;
		hasStagedChanges = true;
	}

	/**
	 * Forces this to be marked as having unstaged changes, no staged changes.
	 */
	public void makeUnstaged()
	{
		this.hasUnstagedChanges = true;
		this.hasStagedChanges = false;
	}

	public void setUnstaged(boolean unstaged)
	{
		this.hasUnstagedChanges = unstaged;
	}

	public boolean hasStagedChanges()
	{
		return hasStagedChanges;
	}

	public boolean hasUnstagedChanges()
	{
		return hasUnstagedChanges;
	}

	public String getCommitBlobSHA()
	{
		return commitBlobSHA;
	}

	public String getCommitBlobMode()
	{
		return commitBlobMode;
	}

	protected String indexInfo()
	{
		Assert.isTrue(status == Status.NEW || commitBlobSHA != null,
				"File is not new, but doesn't have an index entry!"); //$NON-NLS-1$
		if (commitBlobSHA == null)
		{
			return MessageFormat.format("0 0000000000000000000000000000000000000000\t{0}\0", path.toPortableString()); //$NON-NLS-1$
		}

		return MessageFormat.format("{0} {1}\t{2}\0", commitBlobMode, commitBlobSHA, path.toPortableString()); //$NON-NLS-1$
	}

	@Override
	public String toString()
	{
		return MessageFormat
				.format("{0} {1} (Staged? {2}, Unstaged? {3})", status, path.toOSString(), hasStagedChanges, hasUnstagedChanges); //$NON-NLS-1$
	}

	public boolean hasUnmergedChanges()
	{
		return status == Status.UNMERGED;
	}

	public int compareTo(ChangedFile o)
	{
		return path.toPortableString().compareTo(o.path.toPortableString());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ChangedFile)
		{
			ChangedFile other = (ChangedFile) obj;
			return (hasStagedChanges == other.hasStagedChanges) && (hasUnstagedChanges == other.hasUnstagedChanges)
					&& (status == other.status) && (path.equals(other.path));
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 31 + Boolean.valueOf(hasStagedChanges).hashCode();
		hash = hash * 31 + Boolean.valueOf(hasUnstagedChanges).hashCode();
		hash = hash * 31 + status.hashCode();
		hash = hash * 31 + path.hashCode();
		return hash;
	}

	public ChangedFile clone()
	{
		return new ChangedFile(this);
	}

	public ChangedFile merge(ChangedFile other)
	{
		String mode = this.commitBlobMode;
		if (mode == null)
		{
			mode = other.commitBlobMode;
		}

		String sha = this.commitBlobSHA;
		if (sha == null)
		{
			sha = other.commitBlobSHA;
		}
		if (status != other.status)
		{
			IdeLog.logWarning(GitPlugin.getDefault(), "Mismatch statuses when merging. Who wins? " + status.name()
					+ ", " + other.status.name());
		}
		return new ChangedFile(this.repo, this.path, status, mode, sha,
				this.hasStagedChanges || other.hasStagedChanges, this.hasUnstagedChanges || other.hasUnstagedChanges);
	}

	public GitRepository getRepository()
	{
		return repo;
	}
}
