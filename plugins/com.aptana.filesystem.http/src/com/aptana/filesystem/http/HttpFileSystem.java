package com.aptana.filesystem.http;

import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.filesystem.provider.FileSystem;

public class HttpFileSystem extends FileSystem
{

	/**
	 * Subclasses must implement this method to satisfy the contract
	 * of {@link IFileSystem#getStore(URI)}.  If it is not possible to create a file
	 * store corresponding to the provided URI for this file system, a file store
	 * belonging to the null file system should be returned
	 */
	public IFileStore getStore(URI uri)
	{
		return new HttpFileStore(uri);
	}
}
