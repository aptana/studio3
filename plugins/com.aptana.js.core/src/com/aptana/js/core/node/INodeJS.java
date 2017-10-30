/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.node;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.aptana.core.util.IProcessRunner;
import com.aptana.js.core.node.INodeJSService.NodeJsListener;

/**
 * This represents an installation of NodeJS. There may be multiple installs on the user's machine. Just because you
 * have an instance does not guarantee the path exists or that it meets our minimum required version.
 *
 * @author cwilliams
 */
public interface INodeJS extends NodeJsListener
{
	public static final String MIN_NODE_VERSION = "7.6.0"; //$NON-NLS-1$

	/**
	 * Error codes returned by {@link #validate()}
	 */
	public static final int ERR_DOES_NOT_EXIST = 1;
	public static final int ERR_NOT_EXECUTABLE = 2;
	public static final int ERR_INVALID_VERSION = 3;

	/**
	 * Returns an instance of NPM running under this node install.
	 *
	 * @return
	 */
	public INodePackageManager getNPM();

	/**
	 * Path to the NodeJS installation.
	 *
	 * @return
	 */
	public IPath getPath();

	/**
	 * Returns the result of running node -v, typical output will be "v0.10.13"
	 *
	 * @return
	 */
	public String getVersion();

	/**
	 * Does the actual binary exist at the path we're pointing to?
	 *
	 * @return
	 */
	public boolean exists();

	/**
	 * Validates this install of NodeJS. Will return OK if the install exists and meets required minimum version.
	 *
	 * @return
	 */
	public IStatus validate();

	/**
	 * Run something under NodeJS. Under the hood calls out to
	 * {@link IProcessRunner#runInBackground(String, IPath, Map, String...)}
	 *
	 * @param args
	 * @return
	 */
	public IStatus runInBackground(String... args);

	/**
	 * Run something under NodeJS. Under the hood calls out to
	 * {@link IProcessRunner#runInBackground(String, IPath, Map, String...)}
	 *
	 * @param env
	 * @param args
	 * @return
	 */
	public IStatus runInBackground(IPath workingDir, Map<String, String> environment, List<String> args);

	/**
	 * The path to the source code for this install.
	 *
	 * @return
	 */
	public IPath getSourcePath();

	public IStatus downloadSource(IProgressMonitor monitor);
}
