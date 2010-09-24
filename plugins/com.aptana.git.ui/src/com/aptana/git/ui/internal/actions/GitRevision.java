package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.jface.text.revisions.Revision;
import org.eclipse.swt.graphics.RGB;

class GitRevision extends Revision
{

	private String sha;
	private String author;
	private String committer;
	private Date timestamp;
	private String summary;
	private RGB color;

	GitRevision(String sha, String author, String committer, String summary, Date timestamp)
	{
		this.sha = sha;
		this.author = author;
		this.committer = committer;
		this.summary = summary;
		this.timestamp = timestamp;
	}

	@Override
	public Object getHoverInfo()
	{
		return MessageFormat.format("{0}<br />Author: {1}, Committer: {2}", this.summary, this.author, this.committer);
	}

	@Override
	public RGB getColor()
	{
		return color;
	}

	void setColor(RGB newColor)
	{
		this.color = newColor;
	}

	@Override
	public String getId()
	{
		return sha;
	}

	@Override
	public Date getDate()
	{
		return timestamp;
	}

	@Override
	public String getAuthor()
	{
		return author;
	}

}
