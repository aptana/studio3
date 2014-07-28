/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.node;

import java.io.FileFilter;
import java.util.List;
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

	/**
	 * Gets the latest installed version of a package.
	 * 
	 * @param packageName
	 * @return
	 * @throws CoreException
	 */
	public String getInstalledVersion(String packageName) throws CoreException;

	public String getInstalledVersion(String packageName, boolean isGlobal, IPath workingDir) throws CoreException;

	/**
	 * Gets the latest version published for a package. Note that there may be "newer" RC/beta/alphas, but the NPM
	 * "latest" pointer may not refer to them.
	 * 
	 * @param packageName
	 * @return
	 * @throws CoreException
	 */
	public String getLatestVersionAvailable(String packageName) throws CoreException;

	/**
	 * Gets the full list of published versions for a given package. NEVER RETURNS NULL! if anything goes wrong, we'll
	 * throw a CoreException.
	 * 
	 * @param packageName
	 * @return
	 * @throws CoreException
	 */
	public List<String> getAvailableVersions(String packageName) throws CoreException;

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

	/**
	 * Clears the npm cache data. This might help to remove the conflicting dependent packages being referenced by new
	 * installed npm packages.
	 * 
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public IStatus cleanNpmCache(char[] password, boolean runWithSudo, IProgressMonitor monitor);

	/**
	 * Uninstalls an npm package.
	 * 
	 * @param packageName
	 * @param displayName
	 * @param global
	 * @param password
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public IStatus uninstall(String packageName, String displayName, boolean global, char[] password,
			IProgressMonitor monitor) throws CoreException;

	/**
	 * Does the NPM path/install we're pointing to exist?
	 * 
	 * @return
	 */
	public boolean exists();

	/**
	 * The path to the NPM binary script. This may return null if we were unable to find npm!
	 * 
	 * @return
	 */
	public IPath getPath();

	/**
	 * return the version of NPM.
	 * 
	 * @return
	 * @throws CoreException
	 *             if NPM isn't actually installed, or grabbing the version failed.
	 */
	public String getVersion() throws CoreException;

	/**
	 * A way to generically launch commands under NPM. Use sparingly. Ideally we'd have methods to invoke whatever
	 * command you're hacking by using this.
	 * 
	 * @param args
	 * @return
	 * @throws CoreException
	 *             May throw a CoreException to indicate that the NPM path is bad.
	 */
	public IStatus runInBackground(String... args) throws CoreException;

	/**
	 * Search for the npm package installed locally based on the search locations.
	 * 
	 * @param executableName
	 * @param appendExtension
	 * @param searchLocations
	 * @param fileFilter
	 * @return
	 */
	public IPath findNpmPackagePath(String executableName, boolean appendExtension, List<IPath> searchLocations,
			FileFilter fileFilter);
	// TODO Update
}
