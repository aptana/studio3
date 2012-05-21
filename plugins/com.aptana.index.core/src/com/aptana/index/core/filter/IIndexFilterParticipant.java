/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.filter;

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
