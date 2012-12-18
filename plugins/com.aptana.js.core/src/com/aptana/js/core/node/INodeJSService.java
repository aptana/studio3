/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.node;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author cwilliams
 */
public interface INodeJSService
{

	public static interface Listener
	{

		public void nodeJSInstalled();
	}

	public static final String LINUX_DOCS_URL = "http://go.aptana.com/Installing+Node.js"; //$NON-NLS-1$
	public static final String UPGRADE_URL = "http://go.aptana.com/Upgrading+Node.js"; //$NON-NLS-1$

	public static final String MIN_NODE_VERSION = "0.6.0"; //$NON-NLS-1$

	/**
	 * Error codes returned by {@link #acceptBinary(IPath)}
	 */
	public static final int ERR_DOES_NOT_EXIST = 1;
	public static final int ERR_NOT_EXECUTABLE = 2;
	public static final int ERR_INVALID_VERSION = 3;

	/**
	 * Determines if the given path points to a valid nodejs executable (exists, can be run, is in the supported version
	 * range).
	 * 
	 * @param path
	 * @return {@link IStatus}
	 */
	public IStatus acceptBinary(IPath path);

	/**
	 * Searches PATH find the node executable and return it's {@link IPath}. This is not guaranteed to be a valid
	 * version. Please check using {@link #acceptBinary(IPath)}
	 * 
	 * @return
	 */
	public IPath find();

	/**
	 * Returns the path saved in the user's preferences. This is not guaranteed to be a valid version. Please check
	 * using {@link #acceptBinary(IPath)}
	 * 
	 * @return
	 */
	public IPath getSavedPath();

	/**
	 * Attempts to return the {@link #getSavedPath()} if it passes {@link #acceptBinary(IPath)}. Otherwise, attempts to
	 * return path from {@link #find()} if that passes {@link #acceptBinary(IPath)}.
	 * 
	 * @return
	 */
	public IPath getValidExecutable();

	/**
	 * Downloads and then installs NodeJS for the user on Windows and Mac.
	 * 
	 * @param monitor
	 * @return FIXME This seems like maybe it should live in the UI plugin in some sort of INodeJSInstaller interface
	 *         where we have concrete impls for each platform.
	 */
	public IStatus install(char[] password, IProgressMonitor monitor);

	public void addListener(Listener listener);

	public void removeListener(Listener listener);

	/**
	 * Checks validity of the given path as a point to a NodeJS source directory.
	 * 
	 * @param path
	 * @return
	 */
	public IStatus validateSourcePath(IPath path);

	public String getVersion(IPath path);
}
