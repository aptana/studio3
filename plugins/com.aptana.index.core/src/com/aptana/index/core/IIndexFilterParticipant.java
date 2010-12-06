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
