/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.http;

import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileSystem;

public class HttpFileSystem extends FileSystem
{

	/**
	 * Subclasses must implement this method to satisfy the contract
	 * of {@link org.eclipse.core.filesystem.IFileSystem#getStore(URI)}.  If it is not possible to create a file
	 * store corresponding to the provided URI for this file system, a file store
	 * belonging to the null file system should be returned
	 */
	public IFileStore getStore(URI uri)
	{
		return new HttpFileStore(uri);
	}
}
