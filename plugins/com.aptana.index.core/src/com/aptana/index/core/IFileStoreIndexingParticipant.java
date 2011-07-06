/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IFileStoreIndexingParticipant
{
	/**
	 * Index all file stores into the specified index
	 * 
	 * @param resources
	 * @param index
	 * @param monitor
	 * @throws CoreException
	 */
	void index(Set<IFileStore> resources, Index index, IProgressMonitor monitor) throws CoreException;

	/**
	 * The priority is used to determine the order in which multiple indexing participants should run when more than one
	 * indexer is used. Higher numbers run before lower numbers
	 * 
	 * @return
	 */
	int getPriority();

	/**
	 * Set the priority of this indexer
	 * 
	 * @param priority
	 */
	void setPriority(int priority);
}
