/*******************************************************************************
 * Copyright (C) 2006, Robin Rosenberg <robin.rosenberg@dewire.com>
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.aptana.git.internal.core.storage;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.provider.FileRevision;

/**
 * A Git related {@link IFileRevision}. It references a version and a resource, i.e. the version we think corresponds to
 * the resource in specific version.
 */
public abstract class GitFileRevision extends FileRevision
{

	private static final String DEV_NULL = "/dev/null"; //$NON-NLS-1$
	private final String path;

	protected GitFileRevision(final String fileName)
	{
		path = fileName;
	}

	public String getName()
	{
		if (path.equals(DEV_NULL))
		{
			return path;
		}
		final int last = path.lastIndexOf(File.separator);
		return (last >= 0) ? path.substring(last + 1) : path;
	}

	public boolean isPropertyMissing()
	{
		return false;
	}

	public IFileRevision withAllProperties(final IProgressMonitor monitor)
	{
		return this;
	}

	public URI getURI()
	{
		try
		{
			return new URI(null, null, path, null);
		}
		catch (URISyntaxException e)
		{
			return null;
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof GitFileRevision)
		{
			GitFileRevision other = (GitFileRevision) obj;
			return other.path.equals(path);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return path.hashCode();
	}
}
