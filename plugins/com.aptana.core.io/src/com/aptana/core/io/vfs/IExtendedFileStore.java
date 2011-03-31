/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.io.vfs;

import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileTree;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Max Stepanov
 *
 */
public interface IExtendedFileStore extends IFileStore {

	/**
	 * Option flag constant (value 1 &lt;&lt;10) indicating that a fetch
	 * info operation should be detailed
	 * 
	 * @see IFileStore#childInfos(int, IProgressMonitor)
	 * @see IFileStore#fetchInfo(int, IProgressMonitor)
	 */
	public static final int DETAILED = 1 << 10;

	/**
	 * Option flag constant (value 1 &lt;&lt;10) indicating that a fetch
	 * info operation should test only resource existence and type
	 * 
	 * @see IFileStore#childInfos(int, IProgressMonitor)
	 * @see IFileStore#fetchInfo(int, IProgressMonitor)
	 */
	public static final int EXISTENCE = 1 << 11;

	/**
	 * 
	 * @return canonical URI
	 */
	public URI toCanonicalURI();
	
	/**
	 * Fetch file tree
	 * @param visitor
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public IFileTree fetchFileTree(IFileTreeVisitor visitor, IProgressMonitor monitor) throws CoreException;

}
