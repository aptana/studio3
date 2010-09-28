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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Represents a commit in the repo.
 * 
 * @author cwilliams
 */
public class GitCommit
{

	private GitRepository repository;
	private String sha;
	private String subject;
	private long timestamp;
	private String author;
	private String authorEmail;
	private List<String> parentShas;
	private String comment;
	private List<Diff> diffs;

	public GitCommit(GitRepository repository, String sha)
	{
		this.repository = repository;
		this.sha = sha;
	}

	public List<String> parents()
	{
		return parentShas;
	}

	public Date date()
	{
		return new Date(timestamp);
	}

	public String sha()
	{
		return sha;
	}

	public GitRepository repository()
	{
		return repository;
	}

	void setSubject(String subject)
	{
		this.subject = subject;
	}

	void setAuthor(String author)
	{
		this.author = author;
	}

	void setTimestamp(long time)
	{
		this.timestamp = time;
	}

	void setParents(List<String> parents)
	{
		this.parentShas = new ArrayList<String>(parents);
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public String getSubject()
	{
		return subject;
	}

	public String getAuthor()
	{
		return author;
	}

	public String getComment()
	{

		if (comment == null)
			return getSubject();
		StringBuilder builder = new StringBuilder(getSubject());
		builder.append("\n\n").append(comment); //$NON-NLS-1$
		return builder.toString();
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("commit ").append(sha).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
		builder.append(getComment());
		return builder.toString();
	}

	void setComment(String comment)
	{
		this.comment = comment;
	}

	public synchronized List<Diff> getDiff()
	{
		if (diffs == null)
			diffs = Diff.create(this);
		return diffs;
	}

	public GitCommit getFirstParent()
	{
		if (parents() == null || parents().isEmpty())
			return null;
		return new GitCommit(repository, parentShas.get(0));
	}

	public boolean hasParent()
	{
		return parentShas != null && !parentShas.isEmpty();
	}

	public int parentCount()
	{
		if (parentShas == null || parentShas.isEmpty())
			return 0;
		return parentShas.size();
	}

	public Collection<GitRef> getRefs()
	{
		return repository.refs.get(sha);
	}

	/**
	 * Are there any refs associated with this commit?
	 * 
	 * @return
	 */
	public boolean hasRefs()
	{
		return getRefs() != null;
	}

	/**
	 * Number of refs associated with the commit.
	 * 
	 * @return
	 */
	public int refCount()
	{
		if (!hasRefs())
			return 0;
		return getRefs().size();
	}

	public String getAuthorEmail()
	{
		return authorEmail;
	}

	void setAuthorEmail(String authorEmail)
	{
		this.authorEmail = authorEmail;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof GitCommit)
		{
			GitCommit other = (GitCommit) obj;
			return other.sha.equals(sha) && other.repository.equals(repository);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return 31 * sha.hashCode() + repository.hashCode();
	}
}
