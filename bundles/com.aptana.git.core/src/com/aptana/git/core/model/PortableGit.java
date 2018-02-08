/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.git.core.model;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IDebugScopes;

/**
 * @author Max Stepanov
 *
 */
public final class PortableGit {

	private static IPath location;
	private static boolean locationInitialized = false;
	
	/**
	 * 
	 */
	private PortableGit() {
	}
		
	public static IPath getLocation() {
		if (locationInitialized) {
			return location;
		}
		locationInitialized = true;
		Bundle bundle = getBundle();
		if (bundle != null) {
			IPath path = Platform.getStateLocation(bundle);
			if (path != null) {
				path = path.append("bin").append(GitExecutable.GIT_EXECUTABLE_WIN32); //$NON-NLS-1$
			}
			if (path != null && GitExecutable.acceptBinary(path)) {
				return location = path;
			}
		}
		return null;
	}
	
	protected static void checkInstallation(IPath location) {
		Bundle bundle = getBundle();
		if (bundle != null) {
			IPath path = Platform.getStateLocation(bundle);
			if (path != null) {
				path = path.append("bin").append(GitExecutable.GIT_EXECUTABLE_WIN32); //$NON-NLS-1$
			}
			if (path != null && path.equals(location)) {
				Job job = new Job("Check PortableGit installation") { //$NON-NLS-1$
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							getBundle().start(Bundle.START_TRANSIENT);
						} catch (BundleException e) {
							IdeLog.logError(GitPlugin.getDefault(), e, IDebugScopes.DEBUG);
						}
						return Status.OK_STATUS;
					}
				};
				EclipseUtil.setSystemForJob(job);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		}
	}
	
	public static void install() {
		Bundle bundle = getBundle();
		if (bundle != null && bundle.getState() != Bundle.ACTIVE) {
			try {
				bundle.start(Bundle.START_TRANSIENT);
				locationInitialized = false;
			} catch (BundleException e) {
				IdeLog.logError(GitPlugin.getDefault(), e);
			}
		}
	}
	
	private static Bundle getBundle() {
		return Platform.getBundle("com.aptana.portablegit."+Platform.getOS()); //$NON-NLS-1$
	}

}
