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
		builder.append("\n\n").append(comment);
		return builder.toString();
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("commit ").append(sha).append("\n");
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
}
