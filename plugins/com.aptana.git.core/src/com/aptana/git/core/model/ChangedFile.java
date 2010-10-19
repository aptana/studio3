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
