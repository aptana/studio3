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
package com.aptana.git.internal.core.storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.ITag;
import org.eclipse.team.core.history.provider.FileRevision;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRef;

public class CommitFileRevision extends FileRevision
{
	private static final IPath DEV_NULL = Path.fromPortableString("/dev/null"); //$NON-NLS-1$

	private GitCommit commit;
	private IPath path;

	public CommitFileRevision(GitCommit commit, IPath repoRelativePath)
	{
		this.commit = commit;
		this.path = repoRelativePath;
	}

	public IStorage getStorage(IProgressMonitor monitor) throws CoreException
	{
		return new IStorage()
		{

			@SuppressWarnings("rawtypes")
			public Object getAdapter(Class adapter)
			{
				return null;
			}

			public boolean isReadOnly()
			{
				return true;
			}

			public String getName()
			{
				return getName();
			}

			public IPath getFullPath()
			{
				return CommitFileRevision.this.getFullPath();
			}

			public InputStream getContents() throws CoreException
			{
				if (commit == null)
				{
					return new ByteArrayInputStream(new byte[0]);
				}
				try
				{
					Process p = GitExecutable.instance().run(commit.repository().workingDirectory(), "show", //$NON-NLS-1$
							commit.sha() + ":" + path); //$NON-NLS-1$
					return p.getInputStream();
				}
				catch (IOException e)
				{
					throw new CoreException(new Status(IStatus.ERROR, GitPlugin.getPluginId(), e.getMessage(), e));
				}
			}
		};
	}

	public String getName()
	{
		if (path.equals(DEV_NULL))
		{
			return DEV_NULL.toPortableString();
		}
		return path.toPortableString();
	}

	public boolean isPropertyMissing()
	{
		return false;
	}

	public IFileRevision withAllProperties(IProgressMonitor monitor) throws CoreException // NO_UCD
	{
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.team.core.history.IFileState#getURI()
	 */
	public URI getURI()
	{
		if (commit == null)
		{
			return null;
		}
		IPath fullPath = getFullPath();
		if (fullPath == null)
		{
			return null;
		}
		return fullPath.toFile().toURI();
	}

	protected IPath getFullPath()
	{
		if (commit == null)
		{
			return null;
		}
		return commit.repository().workingDirectory().append(path);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.team.core.history.IFileState#getTimestamp()
	 */
	public long getTimestamp()
	{
		if (commit == null)
		{
			return super.getTimestamp();
		}
		return commit.getTimestamp();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.team.core.history.provider.FileRevision#exists()
	 */
	public boolean exists() // NO_UCD
	{
		return !path.equals(DEV_NULL);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.team.core.history.IFileRevision#getContentIdentifier()
	 */
	public String getContentIdentifier()
	{
		if (commit == null)
		{
			return super.getContentIdentifier();
		}
		return commit.sha();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.team.core.history.IFileRevision#getAuthor()
	 */
	public String getAuthor()
	{
		if (commit == null)
		{
			return super.getAuthor();
		}
		return commit.getAuthor();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.team.core.history.IFileRevision#getComment()
	 */
	public String getComment()
	{
		if (commit == null)
		{
			return super.getComment();
		}
		return commit.getComment();
	}

	protected boolean isDescendantOf(IFileRevision revision)
	{
		if (!(revision instanceof CommitFileRevision))
		{
			return false;
		}
		if (commit == null || !commit.hasParent())
		{
			return false;
		}
		CommitFileRevision other = (CommitFileRevision) revision;
		return commit.parents().contains(other.commit.sha());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof CommitFileRevision)
		{
			CommitFileRevision other = (CommitFileRevision) obj;
			return path.equals(other.path) && other.commit.equals(commit);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 31 * path.hashCode();
		hash = hash + ((commit == null) ? 0 : commit.hashCode());
		return hash;
	}

	@Override
	public ITag[] getTags()
	{
		if (commit == null || !commit.hasRefs())
		{
			return super.getTags();
		}
		List<ITag> tags = new ArrayList<ITag>();
		for (GitRef ref : commit.getRefs())
		{
			if (ref.type().equals(GitRef.TYPE.TAG))
			{
				tags.add(new GitTag(ref.shortName()));
			}
		}
		return tags.toArray(new ITag[tags.size()]);
	}

	private class GitTag implements ITag
	{
		private String name;

		GitTag(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}
	}
}
