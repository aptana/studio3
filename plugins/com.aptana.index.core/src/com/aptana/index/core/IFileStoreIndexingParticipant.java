/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.index.core.build.BuildContext;

public interface IFileStoreIndexingParticipant
{

	public static final int DEFAULT_PRIORITY = 50;

	/**
	 * Indexes a single file wrapped up by a context object.
	 * 
	 * @param context
	 * @param index
	 * @param monitor
	 * @throws CoreException
	 */
	void index(BuildContext context, Index index, IProgressMonitor monitor) throws CoreException;

	/**
	 * The priority is used to determine the order in which multiple indexing participants should run when more than one
	 * indexer is used. Higher numbers run before lower numbers
	 * 
	 * @return
	 */
	int getPriority();

}
