package com.aptana.index.core.ui;

import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;

import com.aptana.index.core.IIndexFilterParticipant;

public class IndexFilterParticipant implements IIndexFilterParticipant
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IIndexFilterParticipant#filterFileStores(java.util.Set)
	 */
	public Set<IFileStore> applyFilter(Set<IFileStore> fileStores)
	{
		return IndexFilterManager.getInstance().applyFilter(fileStores);
	}
}
