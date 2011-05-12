/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.jface.text.revisions.Revision;
import org.eclipse.swt.graphics.RGB;

class GitRevision extends Revision
{

	private String sha;
	private String author;
	@SuppressWarnings("unused")
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
		return MessageFormat.format("{0} {1}<br />{2} {3}", this.sha, this.author, this.timestamp, this.summary); //$NON-NLS-1$
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
