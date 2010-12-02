/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.index.core;

import java.net.URI;
import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;

/**
 * Special subclass of IndexContainerJob that ignores the index timestamp and forces all files to be re-indexed.
 * 
 * @author cwilliams
 */
public class RebuildIndexJob extends IndexContainerJob
{

	public RebuildIndexJob(URI containerURI)
	{
		super(containerURI);
	}

	@Override
	protected Set<IFileStore> filterFiles(long indexLastModified, Set<IFileStore> files)
	{
		return files;
	}

}
