package com.aptana.ide.core.io.vfs;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;

public interface IFileTreeVisitor {		

	/**
	 * Returns true to include this file store into resulting file tree and visit its children
	 * or false otherwise.
	 */
	public boolean include(IFileStore store) throws CoreException;

}