/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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

import com.aptana.git.core.GitPlugin;

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
							GitPlugin.logError(e);
						}
						return Status.OK_STATUS;
					}
				};
				job.setSystem(true);
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
				GitPlugin.logError(e);
			}
		}
	}
	
	private static Bundle getBundle() {
		return Platform.getBundle("com.aptana.portablegit."+Platform.getOS()); //$NON-NLS-1$
	}

}
