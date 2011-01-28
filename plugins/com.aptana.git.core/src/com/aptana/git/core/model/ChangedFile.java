/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;

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
	public ChangedFile(ChangedFile other)
	{
		this.path = other.path;
		this.status = other.status;
		this.hasStagedChanges = other.hasStagedChanges;
		this.hasUnstagedChanges = other.hasUnstagedChanges;
		this.commitBlobMode = other.commitBlobMode;
		this.commitBlobSHA = other.commitBlobSHA;
	}

	public ChangedFile(String path, Status status)
	{
		this.path = path;
		this.status = status;
	}

	String path;
	Status status;
	boolean hasStagedChanges;
	boolean hasUnstagedChanges;
	String commitBlobSHA;
	String commitBlobMode;

	// FIXME Use IPath
	public String getPath()
	{
		return new Path(path).toOSString();
	}

	public Status getStatus()
	{
		return status;
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
			return "0 0000000000000000000000000000000000000000\t" + path + "\0"; //$NON-NLS-1$ //$NON-NLS-2$

		return commitBlobMode + " " + commitBlobSHA + "\t" + path + "\0"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public String toString()
	{
		return getStatus() + " " + getPath(); //$NON-NLS-1$
	}

	public boolean hasUnmergedChanges()
	{
		return getStatus().equals(Status.UNMERGED);
	}

	public int compareTo(ChangedFile o)
	{
		return getPath().compareTo(o.getPath());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ChangedFile)
		{
			ChangedFile other = (ChangedFile) obj;
			return (hasStagedChanges == other.hasStagedChanges) && (hasUnstagedChanges == other.hasUnstagedChanges)
					&& status.equals(other.status) && getPath().equals(other.getPath());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 31 + Boolean.valueOf(hasStagedChanges).hashCode();
		hash = hash * 31 + Boolean.valueOf(hasUnstagedChanges).hashCode();
		hash = hash * 31 + getStatus().hashCode();
		hash = hash * 31 + getPath().hashCode();
		return hash;
	}
}
