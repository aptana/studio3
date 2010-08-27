package com.aptana.git.core.model;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;

public class ChangedFile implements Comparable<ChangedFile>
{

	public enum Status
	{
		NEW, DELETED, MODIFIED, UNMERGED
	}

	public ChangedFile(String path)
	{
		this.path = path;
	}

	ChangedFile(ChangedFile other)
	{
		this.path = other.path;
		this.status = other.status;
		this.hasStagedChanges = other.hasStagedChanges;
		this.hasUnstagedChanges = other.hasUnstagedChanges;
		this.commitBlobMode = other.commitBlobMode;
		this.commitBlobSHA = other.commitBlobSHA;
	}

	// Used for unit tests!
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

	public String indexInfo()
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

	@Override
	public int compareTo(ChangedFile o)
	{
		return getPath().compareTo(o.getPath());
	}
}
