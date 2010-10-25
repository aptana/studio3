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
