/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import java.text.MessageFormat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * A value object representing a changed file in a git repo. There can be a large number of these and they can be
 * constantly sorted/hashed/compared - so we try to use direct field references over accessors. We also pre-generate
 * both a portable and OS-specific version of the relative path once.
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
		this(other.portablePath, other.status);
		this.hasStagedChanges = other.hasStagedChanges;
		this.hasUnstagedChanges = other.hasUnstagedChanges;
		this.commitBlobMode = other.commitBlobMode;
		this.commitBlobSHA = other.commitBlobSHA;
	}

	private ChangedFile(String path, Status status)
	{
		this.portablePath = path;
		this.status = status;
		this.osPath = Path.fromPortableString(path).toOSString();
	}

	/**
	 * Creates an instance to the requested ChangedFile instance <code>other</code> only if its path is a relative path.
	 * All resources in the workspace are referenced by relative path. If the requested path is an absolute path, then
	 * it indicates the resource does not belong to the current workspace and we do not need to track the external
	 * resource.
	 * 
	 * @param other
	 * @return
	 */
	public static ChangedFile createInstance(ChangedFile other)
	{
		IPath portablePath = Path.fromOSString(other.portablePath);
		if (portablePath.isAbsolute())
		{
			return null;
		}
		return new ChangedFile(other);
	}

	/**
	 * Creates an instance to the requested <code>path</code> only if it is a relative path. All resources in the
	 * workspace should be referenced by relative path. If the requested path is an absolute path, then it indicates the
	 * resource does not belong to the current workspace and we do not need to track the external resource.
	 * 
	 * @param path
	 * @param status
	 * @return
	 */
	public static ChangedFile createInstance(String path, Status status)
	{
		IPath portablePath = Path.fromOSString(path);
		if (portablePath.isAbsolute())
		{
			return null;
		}
		return new ChangedFile(path, status);
	}

	private String osPath;
	String portablePath;
	Status status;
	boolean hasStagedChanges;
	boolean hasUnstagedChanges;
	String commitBlobSHA;
	String commitBlobMode;

	// FIXME Use IPath
	public String getPath()
	{
		return osPath;
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
		{
			return MessageFormat.format("0 0000000000000000000000000000000000000000\t{0}\0", portablePath); //$NON-NLS-1$
		}

		return MessageFormat.format("{0} {1}\t{2}\0", commitBlobMode, commitBlobSHA, portablePath); //$NON-NLS-1$
	}

	@Override
	public String toString()
	{
		return MessageFormat.format(
				"{0} {1} (Staged? {2}, Unstaged? {3})", status, osPath, hasStagedChanges, hasUnstagedChanges); //$NON-NLS-1$
	}

	public boolean hasUnmergedChanges()
	{
		return status == Status.UNMERGED;
	}

	public int compareTo(ChangedFile o)
	{
		return portablePath.compareTo(o.portablePath);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ChangedFile)
		{
			ChangedFile other = (ChangedFile) obj;
			return (hasStagedChanges == other.hasStagedChanges) && (hasUnstagedChanges == other.hasUnstagedChanges)
					&& (status == other.status) && (portablePath == other.portablePath);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 31 + Boolean.valueOf(hasStagedChanges).hashCode();
		hash = hash * 31 + Boolean.valueOf(hasUnstagedChanges).hashCode();
		hash = hash * 31 + status.hashCode();
		hash = hash * 31 + portablePath.hashCode();
		return hash;
	}
}
