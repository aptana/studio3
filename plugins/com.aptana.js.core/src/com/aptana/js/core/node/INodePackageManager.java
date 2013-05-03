/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.node;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author cwilliams
 */
public interface INodePackageManager
{

	public static final String GLOBAL_ARG = "-g"; //$NON-NLS-1$
	public static final String PARSEABLE_ARG = "-p"; //$NON-NLS-1$

	/**
	 * Installs a package.
	 * 
	 * @param packageName
	 *            The name of the npm package to install
	 * @param displayName
	 *            The UI string to use for the name of the package
	 * @param global
	 *            Whether to install globally
	 * @param password
	 *            The password to pass to sudo if installing globally on Mac/Linux/Unix
	 * @param monitor
	 * @return
	 */
	public IStatus install(String packageName, String displayName, boolean global, char[] password,
			IProgressMonitor monitor);

	/**
	 * Installs a package.
	 * 
	 * @param packageName
	 *            The name of the npm package to install
	 * @param displayName
	 *            The UI string to use for the name of the package
	 * @param global
	 *            Whether to install globally
	 * @param password
	 *            The password to pass to sudo if installing globally on Mac/Linux/Unix
	 * @param workingDirectory
	 * @param monitor
	 * @return
	 */
	public IStatus install(String packageName, String displayName, boolean global, char[] password,
			IPath workingDirectory, IProgressMonitor monitor);

	/**
	 * Lists the installed packages.
	 * 
	 * @param global
	 * @return
	 * @throws CoreException
	 *             if an error occurs trying to generate the listing
	 */
	public Set<String> list(boolean global) throws CoreException;

	/**
	 * Determines if a package is installed. Checks both local and global package listings.
	 * 
	 * @param packageName
	 * @return
	 * @throws CoreException
	 */
	public boolean isInstalled(String packageName) throws CoreException;

	/**
	 * Retrieves the node modules path for the specific package.
	 * 
	 * @param packageName
	 *            the package to get the path for
	 * @return the node modules path
	 * @throws CoreException
	 */
	public IPath getModulesPath(String packageName) throws CoreException;

	public String getInstalledVersion(String packageName) throws CoreException;

	public String getLatestVersionAvailable(String packageName) throws CoreException;

	public IPath findNPM();

	public String getConfigValue(String key) throws CoreException;

	/**
	 * Returns the location where node packages' binary scripts get installed.
	 * 
	 * @return
	 * @throws CoreException
	 */
	public IPath getBinariesPath() throws CoreException;

	/**
	 * Returns the location where node modules get installed.
	 * 
	 * @return
	 * @throws CoreException
	 */
	public IPath getModulesPath() throws CoreException;

	/**
	 * Returns the prefix path of npm. The prefix path can be configured either in the environment variable
	 * NPM_CONFIG_PREFIX, or through the 'prefix' attribute from the npm configuration file.
	 * 
	 * @return
	 * @throws CoreException
	 */
	public IPath getConfigPrefixPath() throws CoreException;

	// TODO Uninstall
	// TODO Update
}
