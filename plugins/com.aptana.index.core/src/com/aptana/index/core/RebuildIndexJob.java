/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
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
	protected Set<IFileStore> filterFilesByTimestamp(long indexLastModified, Set<IFileStore> files)
	{
		return files;
	}

}
