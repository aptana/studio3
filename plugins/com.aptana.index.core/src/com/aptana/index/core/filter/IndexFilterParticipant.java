/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.filter;

import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;


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
