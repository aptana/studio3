/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portablegit.win32;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PortableGitPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.portablegit.win32"; //$NON-NLS-1$

	// The shared instance
	private static PortableGitPlugin plugin;
	
	/**
	 * The constructor
	 */
	public PortableGitPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			checkOrInstallGit();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	private void checkOrInstallGit() {
		try {
			IPath path = getStateLocation();
			IPath installed = path.append(".installed_"+getBundle().getVersion().toString()); //$NON-NLS-1$
			if (!installed.toFile().exists()) {
				path.toFile().mkdirs();
				if (Extractor.extract(path)) {
					installed.toFile().createNewFile();
				}
			}
		} catch (IOException e) {
			log(e);
		}
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PortableGitPlugin getDefault() {
		return plugin;
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, e.getLocalizedMessage(), e));
	}

	public static void log(String msg) {
		log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, null));
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

}
