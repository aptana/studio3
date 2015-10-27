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
 * This is a service that handles installing NodeJS itself, checking if it's installed, detecting existing
 * installations, Verifying valid executables, listening for installs, etcs.
 *
 * @author cwilliams
 */
public interface INodeJSService
{

	public static interface NodeJsListener
	{
		public void nodeJSInstalled();
	}

	public static final String LINUX_DOCS_URL = "http://go.appcelerator.com/Installing+Node.js"; //$NON-NLS-1$

	public static final String NODE = "node"; //$NON-NLS-1$
	public static final String NPM = "npm"; //$NON-NLS-1$

	/**
	 * Searches PATH find the node executable and return it. This is not guaranteed to be a valid version. Please check
	 * using {@link INodeJS#validate()}. This value should really only be used to display the detected version! Use
	 * {@link #getValidExecutable()} to get the install to use for commands! May return null if no install detected!
	 *
	 * @return
	 */
	public INodeJS detectInstall();

	/**
	 * Returns the NodeJS install that user set path to in preferences. This is not guaranteed to be a valid version.
	 * Please check using {@link INodeJS#validate()}. To actually run commands against executable use
	 * {@link #getValidExecutable()}
	 *
	 * @return
	 */
	public INodeJS getInstallFromPreferences();

	/**
	 * Attempts to return the {@link #getInstallFromPreferences()} if it passes validation. Otherwise, attempts to
	 * return path from {@link #detectInstall()} if that passes validation. This is what should be used to execute node
	 * commands.
	 *
	 * @return
	 */
	public INodeJS getValidExecutable();

	/**
	 * Downloads and then installs NodeJS for the user on Windows and Mac.
	 *
	 * @param monitor
	 * @return FIXME This seems like maybe it should live in the UI plugin in some sort of INodeJSInstaller interface
	 *         where we have concrete impls for each platform.
	 */
	public IStatus install(char[] password, IProgressMonitor monitor);

	public void addListener(NodeJsListener listener);

	public void removeListener(NodeJsListener listener);

	/**
	 * Checks validity of the given path as a point to a NodeJS source directory.
	 *
	 * @param path
	 * @return
	 */
	public IStatus validateSourcePath(IPath path);

	/**
	 * Checks whether NodeJS is already installed on the machine.
	 *
	 * @return true if NodeJS is already available on the machine.
	 */
	public boolean isInstalled();

	/**
	 * Calls {@link INodeJS#validate()} for the path.
	 *
	 * @param fromOSString
	 * @return
	 */
	public IStatus acceptBinary(IPath nodeJSBinary);
}
