/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.node;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.osgi.framework.Version;

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
	 *            The name of the npm package to install
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

	public Version getInstalledVersion(String packageName) throws CoreException;

	public Version getLatestVersionAvailable(String packageName) throws CoreException;

	public IPath findNPM();

	public String getConfigValue(String key) throws CoreException;

	boolean isNpmConfigWritable();

	public List<IPath> getPackagesInstallLocations();

	// TODO Uninstall
	// TODO Update
}
