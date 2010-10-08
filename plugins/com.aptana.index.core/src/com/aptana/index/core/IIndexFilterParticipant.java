package com.aptana.index.core;

import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;

public interface IIndexFilterParticipant
{
	/**
	 * Filters out any file stores that should not be indexed
	 * 
	 * @param fileStores
	 * @return
	 */
	public Set<IFileStore> applyFilter(Set<IFileStore> fileStores);
}
